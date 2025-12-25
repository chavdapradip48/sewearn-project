package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.payment.*;
import com.pradip.sewearn.enums.AdjustmentType;
import com.pradip.sewearn.enums.SettlementStatus;
import com.pradip.sewearn.exception.custom.BusinessValidationException;
import com.pradip.sewearn.exception.custom.InvalidPaymentOperationException;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.exception.custom.SettlementOverlapException;
import com.pradip.sewearn.mapper.SettlementMapper;
import com.pradip.sewearn.model.payment.ReceivedPayment;
import com.pradip.sewearn.model.payment.Settlement;
import com.pradip.sewearn.model.payment.SettlementAdjustment;
import com.pradip.sewearn.model.payment.SettlementPaymentAllocation;
import com.pradip.sewearn.model.submit.SewEarnSubmit;
import com.pradip.sewearn.repository.payment.ReceivedPaymentRepository;
import com.pradip.sewearn.repository.payment.SettlementAdjustmentRepository;
import com.pradip.sewearn.repository.payment.SettlementPaymentAllocationRepository;
import com.pradip.sewearn.repository.payment.SettlementRepository;
import com.pradip.sewearn.repository.submit.SewEarnSubmitRepository;
import com.pradip.sewearn.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementAdjustmentRepository adjustmentRepository;
    private final ReceivedPaymentRepository receivedPaymentRepository;
    private final SettlementPaymentAllocationRepository allocationRepository;
    private final SewEarnSubmitRepository submitRepository;

    /* =========================================================
       1️⃣ CALCULATE (PREVIEW ONLY – NO DB WRITE)
       ========================================================= */
    @Override
    @Transactional(readOnly = true)
    public SettlementCalculateResponse calculate(SettlementCalculateRequest request) {

        // ---- basic validation (DTO validation already covers nulls) ----
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessValidationException(
                    "Start date must be before or equal to end date"
            );
        }

        // ---- prevent overlapping settlements ----
        boolean overlap = settlementRepository.existsOverlappingSettlement(
                request.getStartDate(),
                request.getEndDate(),
                null
        );
        if (overlap) {
            throw new SettlementOverlapException(
                    "Settlement already exists for the selected date range"
            );
        }

        // ---- FETCH SUBMISSIONS (SOURCE OF TRUTH) ----
        List<SewEarnSubmit> submissions =
                submitRepository.findBySubmissionDateBetween(
                        request.getStartDate(),
                        request.getEndDate()
                );

        if (submissions.isEmpty()) {
            throw new BusinessValidationException(
                    "No submissions found for the selected date range"
            );
        }

        // ---- CALCULATIONS ----

        Long receivableAmount = submissions.stream()
                .map(SewEarnSubmit::getTotalEarning)
                .reduce(0L, Long::sum);

        Integer totalQuantity = submissions.stream()
                .map(SewEarnSubmit::getTotalQuantity)
                .reduce(0, Integer::sum);

        Integer submittedTimesInDay = (int) submissions.stream()
                .map(SewEarnSubmit::getSubmissionDate)
                .distinct()
                .count();

        // ---- PREVIOUS PENDING AMOUNT ----
        Long previousPendingAmount = settlementRepository
                .findTopByOrderByPeriodEndDateDesc()
                .map(s -> {
                    Long pending = s.getReceivableAmount() - s.getTotalReceivedAmount();
                    return pending > 0 ? pending : 0L;
                })
                .orElse(0L);

        // ---- RESPONSE ----
        return SettlementCalculateResponse.builder()
                .periodStartDate(request.getStartDate())
                .periodEndDate(request.getEndDate())
                .receivableAmount(receivableAmount)
                .previousPendingAmount(previousPendingAmount)
                .totalQuantity(totalQuantity)
                .submittedTimesInDay(submittedTimesInDay)
                .build();
    }

    /* =========================================================
       2️⃣ CREATE SETTLEMENT
       ========================================================= */

    @Override
    public SettlementResponse create(SettlementCreateRequest request) {

        if (request.getPeriodStartDate() == null || request.getPeriodEndDate() == null) {
            throw new BusinessValidationException("Settlement period dates are required");
        }

        if (request.getPeriodStartDate().isAfter(request.getPeriodEndDate())) {
            throw new BusinessValidationException("Invalid settlement date range");
        }

        boolean overlap = settlementRepository.existsOverlappingSettlement(
                request.getPeriodStartDate(),
                request.getPeriodEndDate(),
                null
        );
        if (overlap) {
            throw new SettlementOverlapException(
                    "Settlement already exists for the selected date range"
            );
        }

        Settlement settlement = Settlement.builder()
                .periodStartDate(request.getPeriodStartDate())
                .periodEndDate(request.getPeriodEndDate())
                .calculatedDate(LocalDate.now())
                .submittedTimesInDay(request.getSubmittedTimesInDay())
                .totalQuantity(request.getTotalQuantity())
                .receivableAmount(request.getReceivableAmount())
                .totalReceivedAmount(0L)
                .status(SettlementStatus.CALCULATED)
                .build();

        settlementRepository.save(settlement);

        /* -------- Save Adjustments -------- */

        if (request.getAdjustments() != null) {
            for (SettlementAdjustmentRequest adjReq : request.getAdjustments()) {

                if (adjReq.getAmount() == null || adjReq.getAmount() <= 0) {
                    throw new BusinessValidationException(
                            "Adjustment amount must be greater than zero"
                    );
                }

                adjustmentRepository.save(
                        SettlementMapper.toEntity(adjReq, settlement)
                );
            }
        }

        /* -------- Save Payments + Allocation -------- */

        if (request.getReceivedPayments() != null) {
            for (ReceivedPaymentRequest payReq : request.getReceivedPayments()) {

                if (payReq.getReceivedAmount() == null || payReq.getReceivedAmount() <= 0) {
                    throw new InvalidPaymentOperationException(
                            "Received payment amount must be greater than zero"
                    );
                }

                ReceivedPayment payment = receivedPaymentRepository.save(
                        SettlementMapper.toEntity(payReq)
                );

                allocatePayment(payment);
            }
        }

        updateSettlementTotalsAndStatus(settlement.getId());

        return SettlementMapper.toResponse(settlement);
    }

    /* =========================================================
       3️⃣ UPDATE SETTLEMENT (LIMITED FIELDS)
       ========================================================= */

    @Override
    public SettlementResponse update(Long settlementId, SettlementUpdateRequest request) {

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Settlement not found with id: " + settlementId
                        )
                );

        boolean overlap = settlementRepository.existsOverlappingSettlement(
                request.getPeriodStartDate(),
                request.getPeriodEndDate(),
                settlementId
        );
        if (overlap) {
            throw new SettlementOverlapException(
                    "Another settlement already exists for the selected date range"
            );
        }

        settlement.setPeriodStartDate(request.getPeriodStartDate());
        settlement.setPeriodEndDate(request.getPeriodEndDate());
        settlement.setSubmittedTimesInDay(request.getSubmittedTimesInDay());
        settlement.setTotalQuantity(request.getTotalQuantity());
        settlement.setReceivableAmount(request.getReceivableAmount());

        settlementRepository.save(settlement);
        updateSettlementTotalsAndStatus(settlementId);

        return SettlementMapper.toResponse(settlement);
    }

    /* =========================================================
       4️⃣ GET SETTLEMENT DETAIL
       ========================================================= */

    @Override
    @Transactional(readOnly = true)
    public SettlementDetailResponse getById(Long settlementId) {

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Settlement not found with id: " + settlementId
                        )
                );

        List<SettlementAdjustment> adjustments =
                adjustmentRepository.findBySettlementId(settlementId);

        List<ReceivedPayment> payments =
                receivedPaymentRepository.findBySettlementId(settlementId);

        List<SettlementPaymentAllocation> allocations =
                allocationRepository.findBySettlementId(settlementId);

        Long netPayable = calculateNetPayable(settlement, adjustments);
        Long pendingAmount = netPayable - settlement.getTotalReceivedAmount();

        return SettlementMapper.toDetailResponse(
                settlement,
                netPayable,
                pendingAmount,
                adjustments,
                payments,
                allocations
        );
    }

    /* =========================================================
       5️⃣ SUMMARY LIST
       ========================================================= */

    @Override
    @Transactional(readOnly = true)
    public List<SettlementSummaryResponse> getSummaryList() {

        return settlementRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Settlement::getPeriodStartDate).reversed())
                .map(SettlementMapper::toSummary)
                .toList();
    }

    /* =========================================================
       🔒 INTERNAL HELPERS
       ========================================================= */

    private Long calculateNetPayable(
            Settlement settlement,
            List<SettlementAdjustment> adjustments
    ) {
        long extra = adjustments.stream()
                .filter(a -> a.getType() == AdjustmentType.EXTRA)
                .mapToLong(SettlementAdjustment::getAmount)
                .sum();

        long expense = adjustments.stream()
                .filter(a -> a.getType() == AdjustmentType.EXPENSE)
                .mapToLong(SettlementAdjustment::getAmount)
                .sum();

        return settlement.getReceivableAmount() + extra - expense;
    }

    /**
     * Allocate payment amount to pending settlements (oldest first)
     */
    private void allocatePayment(ReceivedPayment payment) {

        long remaining = payment.getReceivedAmount();

        List<Settlement> settlements = settlementRepository.findAll()
                .stream()
                .filter(s -> s.getStatus() != SettlementStatus.PAID)
                .sorted(Comparator.comparing(Settlement::getPeriodStartDate))
                .toList();

        for (Settlement settlement : settlements) {

            long netPayable = calculateNetPayable(
                    settlement,
                    adjustmentRepository.findBySettlementId(settlement.getId())
            );

            long alreadyReceived =
                    allocationRepository.sumAllocatedAmountBySettlementId(settlement.getId());

            long pending = netPayable - alreadyReceived;

            if (pending <= 0) continue;

            long allocate = Math.min(remaining, pending);

            allocationRepository.save(
                    SettlementPaymentAllocation.builder()
                            .settlement(settlement)
                            .receivedPayment(payment)
                            .allocatedAmount(allocate)
                            .build()
            );

            remaining -= allocate;
            if (remaining == 0) break;
        }

        if (remaining > 0) {
            throw new InvalidPaymentOperationException(
                    "Received amount exceeds total pending settlement amount"
            );
        }
    }

    /**
     * Update cached totalReceivedAmount and settlement status
     */
    private void updateSettlementTotalsAndStatus(Long settlementId) {

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Settlement not found with id: " + settlementId
                        )
                );

        Long totalReceived =
                allocationRepository.sumAllocatedAmountBySettlementId(settlementId);

        settlement.setTotalReceivedAmount(totalReceived);

        Long netPayable = calculateNetPayable(
                settlement,
                adjustmentRepository.findBySettlementId(settlementId)
        );

        if (totalReceived == 0) {
            settlement.setStatus(SettlementStatus.CALCULATED);
        } else if (totalReceived < netPayable) {
            settlement.setStatus(SettlementStatus.PARTIAL);
        } else {
            settlement.setStatus(SettlementStatus.PAID);
        }

        settlementRepository.save(settlement);
    }
}
