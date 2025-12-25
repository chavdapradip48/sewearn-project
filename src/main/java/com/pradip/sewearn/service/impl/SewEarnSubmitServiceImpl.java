package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.AllocationContext;
import com.pradip.sewearn.dto.submit.*;
import com.pradip.sewearn.exception.custom.BadRequestException;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.SewEarnSubmitMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.receive.ReceivedItem;
import com.pradip.sewearn.model.submit.SubmitItem;
import com.pradip.sewearn.model.submit.SubmitItemDetail;
import com.pradip.sewearn.model.submit.SewEarnSubmit;
import com.pradip.sewearn.projection.AwaitingProjection;
import com.pradip.sewearn.repository.*;
import com.pradip.sewearn.service.SewEarnSubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SewEarnSubmitServiceImpl implements SewEarnSubmitService {

    private final SewEarnSubmitRepository submitRepository;
    private final SubmitItemDetailRepository submitItemDetailRepository;
    private final RawMaterialTypeRepository materialRepository;
    private final ReceivedItemRepository receivedItemRepository;
    private final AllocationService  allocationService;

    private final SewEarnSubmitMapper mapper;

    // ============================================================
    // CREATE SUBMISSION
    // ============================================================
    @Override
    public SewEarnSubmitResponse createSubmission(SewEarnSubmitRequest request) {

        AllocationContext ctx = allocationService.prepareContext(request);

        allocationService.validate(ctx);
        allocationService.applyAllocations(ctx);

        SewEarnSubmit saved = submitRepository.save(ctx.getSubmit());
        return mapper.toDto(saved);
    }

    // ============================================================
    // READ
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public SewEarnSubmitResponse getSubmissionById(Long id) {
        SewEarnSubmit entity = submitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submit not found with id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmit> getAllSubmissions(Pageable pageable) {
        return submitRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitResponse> getSubmissionsByDate(java.time.LocalDate date, Pageable pageable) {
        return submitRepository.findBySubmissionDate(date, pageable).map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitResponse> getSubmissionsByDateRange(java.time.LocalDate start, java.time.LocalDate end, Pageable pageable) {
        return submitRepository.findBySubmissionDateBetween(start, end, pageable).map(mapper::toDto);
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Override
    public SewEarnSubmitResponse updateSubmission(Long id, SewEarnSubmitRequest request) {

        SewEarnSubmit existing = submitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submit not found: " + id));

        submitItemDetailRepository.deleteBySubmitId(id);
        existing.getSubmittedItems().clear();

        AllocationContext ctx = allocationService.prepareContext(request);
        ctx.setSubmit(existing);

        allocationService.validate(ctx);
        allocationService.applyAllocations(ctx);

        SewEarnSubmit saved = submitRepository.save(existing);
        return mapper.toDto(saved);
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Override
    public void deleteSubmission(Long id) {

        SewEarnSubmit existing = submitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submit not found with id: " + id));

        List<SubmitItemDetail> details = existing.getSubmittedItems()
                .stream()
                .flatMap(si -> si.getDetails().stream())
                .toList();

        if (!details.isEmpty()) {
            submitItemDetailRepository.deleteAll(details);
        }

        submitRepository.delete(existing);
    }

    // ============================================================
    // SUMMARY LIST
    // ============================================================
    @Override
    @Transactional(readOnly = true)
    public List<SewEarnSubmitSummaryResponse> getAllSubmissionsSummary() {
        return submitRepository.findAll().stream()
                .map(mapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitSummaryResponse> getAllSubmissionsSummaryPaged(Pageable pageable) {
        return submitRepository.findAll(pageable).map(mapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AwaitingSubmissionMaterialResponse> getAwaitingForSubmission() {

        List<AwaitingProjection> rows = submitItemDetailRepository.findAwaitingSummary();

        Map<Long, AwaitingSubmissionMaterialResponse> materialMap = new LinkedHashMap<>();

        for (AwaitingProjection row : rows) {

            int received     = row.getReceivedQuantity();
            int completed    = row.getCompletedQuantity();
            int submitted    = row.getSubmittedQuantity() == null ? 0 : row.getSubmittedQuantity();

            // business rule 1: skip if nothing is completed
            if (completed == 0) continue;

            // business rule 2: skip if fully submitted
            if (completed == submitted) continue;

            // business rule 3: pending calculation for display
            int pending = received - completed;
            if (pending < 0) pending = 0;  // safety guard

            // group by materialId
            materialMap.putIfAbsent(
                    row.getMaterialId(),
                    AwaitingSubmissionMaterialResponse.builder()
                            .materialId(row.getMaterialId())
                            .materialName(row.getMaterialName())
                            .totalPendingQuantity(0)
                            .batches(new ArrayList<>())
                            .build()
            );

            AwaitingSubmissionMaterialResponse materialResponse = materialMap.get(row.getMaterialId());

            // add batch record
            materialResponse.getBatches().add(
                    AwaitingSubmissionBatchResponse.builder()
                            .receivedItemId(row.getReceivedItemId())
                            .receivedDate(row.getReceivedDate())
                            .receivedQuantity(received)
                            .completedQuantity(completed)
                            .alreadySubmitted(submitted)
                            .pendingQuantity(pending)
                            .build()
            );

            // update total pending for this material
            materialResponse.setTotalPendingQuantity(
                    materialResponse.getTotalPendingQuantity() + pending
            );
        }

        return new ArrayList<>(materialMap.values());
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitSummaryResponse> getSubmissionsByDateSummary(java.time.LocalDate date, Pageable pageable) {
        return submitRepository.findBySubmissionDate(date, pageable).map(mapper::toSummary);
    }

    // ============================================================
    // VALIDATION + ATTACH DETAIL ITEMS
    // ============================================================
    private void validateAndAttachBatchDetails(SubmitItem submitItem, SubmitItemRequest itemReq) {

        // 1. duplicate ReceivedItemId detection
        List<Long> batchIds = itemReq.getBatches().stream()
                .map(SubmitBatchRequest::getReceivedItemId)
                .toList();

        if (new HashSet<>(batchIds).size() != batchIds.size()) {
            throw new BadRequestException("Duplicate receivedItemId detected in batches for materialId: "
                    + itemReq.getMaterialId());
        }

        // 2. sum must match
        int batchTotal = itemReq.getBatches()
                .stream()
                .mapToInt(SubmitBatchRequest::getQuantity)
                .sum();

        if (batchTotal != itemReq.getTotalSubmitQuantity()) {
            throw new BadRequestException("Batch sum (" + batchTotal
                    + ") does not match totalSubmitQuantity ("
                    + itemReq.getTotalSubmitQuantity() + ")");
        }

        // 3. load batches
        List<ReceivedItem> receivedItems = receivedItemRepository.findAllByIdIn(batchIds);

        if (receivedItems.size() != batchIds.size()) {
            throw new BadRequestException("One or more receivedItemId(s) do not exist.");
        }

        Map<Long, ReceivedItem> receivedMap = receivedItems.stream()
                .collect(Collectors.toMap(ReceivedItem::getId, r -> r));

        // 4. validate each batch & create details
        for (SubmitBatchRequest batchReq : itemReq.getBatches()) {

            ReceivedItem r = receivedMap.get(batchReq.getReceivedItemId());

            if (!Objects.equals(r.getRawMaterialType().getId(), itemReq.getMaterialId())) {
                throw new BadRequestException("Received batch " + r.getId()
                        + " does not belong to materialId " + itemReq.getMaterialId());
            }

            int completed = Optional.ofNullable(r.getTotalCompletedQuantity()).orElse(0);
            int alreadySubmitted = submitItemDetailRepository.sumSubmittedForReceivedItem(r.getId());
            int available = completed - alreadySubmitted;

            if (batchReq.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be > 0 for receivedItemId: " + r.getId());
            }

            if (batchReq.getQuantity() > available) {
                throw new BadRequestException("Batch " + r.getId()
                        + " has only " + available + " available, requested: "
                        + batchReq.getQuantity());
            }

            SubmitItemDetail detail = SubmitItemDetail.builder()
                    .receivedItem(r)
                    .quantity(batchReq.getQuantity())
                    .receivedDate(r.getReceive().getReceivedDate())
                    .submitItem(submitItem)
                    .build();

            submitItem.addDetail(detail);
        }
    }
}
