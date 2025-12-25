package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.payment.*;
import com.pradip.sewearn.model.payment.ReceivedPayment;
import com.pradip.sewearn.model.payment.Settlement;
import com.pradip.sewearn.model.payment.SettlementAdjustment;
import com.pradip.sewearn.model.payment.SettlementPaymentAllocation;

import java.util.List;
import java.util.stream.Collectors;

public final class SettlementMapper {

    private SettlementMapper() {
    }

    /* =========================
       ENTITY → RESPONSE
       ========================= */

    public static SettlementResponse toResponse(Settlement settlement) {
        return SettlementResponse.builder()
                .id(settlement.getId())
                .periodStartDate(settlement.getPeriodStartDate())
                .periodEndDate(settlement.getPeriodEndDate())
                .calculatedDate(settlement.getCalculatedDate())
                .submittedTimesInDay(settlement.getSubmittedTimesInDay())
                .totalQuantity(settlement.getTotalQuantity())
                .receivableAmount(settlement.getReceivableAmount())
                .totalReceivedAmount(settlement.getTotalReceivedAmount())
                .status(settlement.getStatus())
                .build();
    }

    public static SettlementSummaryResponse toSummary(Settlement settlement) {
        return SettlementSummaryResponse.builder()
                .id(settlement.getId())
                .periodStartDate(settlement.getPeriodStartDate())
                .periodEndDate(settlement.getPeriodEndDate())
                .receivableAmount(settlement.getReceivableAmount())
                .totalReceivedAmount(settlement.getTotalReceivedAmount())
                .status(settlement.getStatus())
                .build();
    }

    public static SettlementDetailResponse toDetailResponse(
            Settlement settlement,
            Long netPayableAmount,
            Long pendingAmount,
            List<SettlementAdjustment> adjustments,
            List<ReceivedPayment> receivedPayments,
            List<SettlementPaymentAllocation> allocations
    ) {

        return SettlementDetailResponse.builder()
                .id(settlement.getId())
                .periodStartDate(settlement.getPeriodStartDate())
                .periodEndDate(settlement.getPeriodEndDate())
                .calculatedDate(settlement.getCalculatedDate())
                .submittedTimesInDay(settlement.getSubmittedTimesInDay())
                .totalQuantity(settlement.getTotalQuantity())
                .receivableAmount(settlement.getReceivableAmount())
                .totalReceivedAmount(settlement.getTotalReceivedAmount())
                .netPayableAmount(netPayableAmount)
                .pendingAmount(pendingAmount)
                .status(settlement.getStatus())
                .adjustments(toAdjustmentResponses(adjustments))
                .receivedPayments(toPaymentResponses(receivedPayments))
                .allocations(toAllocationResponses(allocations))
                .build();
    }

    /* =========================
       ADJUSTMENT MAPPING
       ========================= */

    public static SettlementAdjustment toEntity(
            SettlementAdjustmentRequest request,
            Settlement settlement
    ) {
        return SettlementAdjustment.builder()
                .settlement(settlement)
                .description(request.getDescription())
                .amount(request.getAmount())
                .type(request.getType())
                .build();
    }

    public static SettlementAdjustmentResponse toResponse(
            SettlementAdjustment adjustment
    ) {
        return SettlementAdjustmentResponse.builder()
                .id(adjustment.getId())
                .description(adjustment.getDescription())
                .amount(adjustment.getAmount())
                .type(adjustment.getType())
                .build();
    }

    public static List<SettlementAdjustmentResponse> toAdjustmentResponses(
            List<SettlementAdjustment> adjustments
    ) {
        return adjustments.stream()
                .map(SettlementMapper::toResponse)
                .collect(Collectors.toList());
    }

    /* =========================
       RECEIVED PAYMENT MAPPING
       ========================= */

    public static ReceivedPayment toEntity(
            ReceivedPaymentRequest request
    ) {
        return ReceivedPayment.builder()
                .receivedDate(request.getReceivedDate())
                .receivedAmount(request.getReceivedAmount())
                .paymentMode(request.getPaymentMode())
                .reference(request.getReference())
                .build();
    }

    public static ReceivedPaymentResponse toResponse(
            ReceivedPayment payment
    ) {
        return ReceivedPaymentResponse.builder()
                .id(payment.getId())
                .receivedDate(payment.getReceivedDate())
                .receivedAmount(payment.getReceivedAmount())
                .paymentMode(payment.getPaymentMode())
                .reference(payment.getReference())
                .build();
    }

    public static List<ReceivedPaymentResponse> toPaymentResponses(
            List<ReceivedPayment> payments
    ) {
        return payments.stream()
                .map(SettlementMapper::toResponse)
                .collect(Collectors.toList());
    }

    /* =========================
       ALLOCATION MAPPING
       ========================= */

    public static SettlementAllocationResponse toResponse(
            SettlementPaymentAllocation allocation
    ) {
        return SettlementAllocationResponse.builder()
                .id(allocation.getId())
                .settlementId(allocation.getSettlement().getId())
                .receivedPaymentId(allocation.getReceivedPayment().getId())
                .allocatedAmount(allocation.getAllocatedAmount())
                .build();
    }

    public static List<SettlementAllocationResponse> toAllocationResponses(
            List<SettlementPaymentAllocation> allocations
    ) {
        return allocations.stream()
                .map(SettlementMapper::toResponse)
                .collect(Collectors.toList());
    }
}
