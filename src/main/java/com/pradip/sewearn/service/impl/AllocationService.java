package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.AllocationContext;
import com.pradip.sewearn.dto.submit.SewEarnSubmitRequest;
import com.pradip.sewearn.dto.submit.SubmitBatchRequest;
import com.pradip.sewearn.dto.submit.SubmitItemRequest;
import com.pradip.sewearn.mapper.SewEarnSubmitMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.receive.ReceivedItem;
import com.pradip.sewearn.model.submit.SubmitItem;
import com.pradip.sewearn.model.submit.SubmitItemDetail;
import com.pradip.sewearn.projection.SubmittedSummaryProjection;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.repository.ReceivedItemRepository;
import com.pradip.sewearn.repository.SubmitItemDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final ReceivedItemRepository receivedItemRepository;
    private final SubmitItemDetailRepository detailRepository;
    private final RawMaterialTypeRepository materialRepository;
    private final SewEarnSubmitMapper mapper;

    public AllocationContext prepareContext(SewEarnSubmitRequest request) {

        List<Long> batchIds = request.getItems().stream()
                .flatMap(i -> i.getBatches().stream().map(SubmitBatchRequest::getReceivedItemId))
                .toList();

        // Fetch batches
        List<ReceivedItem> received = receivedItemRepository.findAllByIdIn(batchIds);

        // Fetch submitted summary
        Map<Long, Integer> submittedMap =
                detailRepository.getSubmittedSummaryForBatchIds(batchIds)
                        .stream()
                        .collect(Collectors.toMap(
                                SubmittedSummaryProjection::getReceivedItemId,
                                p -> Optional.ofNullable(p.getSubmittedQty()).orElse(0)
                        ));

        return AllocationContext.builder()
                .submit(mapper.toEntity(request))
                .request(request)
                .receivedMap(
                        received.stream().collect(Collectors.toMap(ReceivedItem::getId, r -> r))
                )
                .alreadySubmittedMap(submittedMap)
                .build();
    }

    public void validate(AllocationContext ctx) {
        // move ALL existing validation logic here
        // small, readable, modular methods
        // validateItem()
        // validateBatch()
        // validateQuantity()
        // etc.
    }

    public void applyAllocations(AllocationContext ctx) {

        int totalQty = 0;
        long totalEarning = 0L;

        for (SubmitItemRequest itemReq : ctx.getRequest().getItems()) {

            RawMaterialType material = materialRepository.findById(itemReq.getMaterialId())
                    .orElseThrow();

            SubmitItem item = mapper.toItemEntity(itemReq);
            item.setRawMaterialType(material);
            item.setSubmit(ctx.getSubmit());

            for (SubmitBatchRequest b : itemReq.getBatches()) {

                ReceivedItem r = ctx.getReceivedMap().get(b.getReceivedItemId());
                int alreadySubmitted = ctx.getAlreadySubmittedMap().getOrDefault(b.getReceivedItemId(), 0);

                SubmitItemDetail detail = SubmitItemDetail.builder()
                        .receivedItem(r)
                        .quantity(b.getQuantity())
                        .receivedDate(r.getReceive().getReceivedDate())
                        .submitItem(item)
                        .build();

                item.addDetail(detail);

                ctx.getAlreadySubmittedMap()
                        .put(b.getReceivedItemId(), alreadySubmitted + b.getQuantity());
            }

            totalQty += item.getQuantity();
            totalEarning += (long) item.getQuantity() * material.getPrice();

            ctx.getSubmit().addSubmittedItem(item);
        }

        ctx.getSubmit().setTotalQuantity(totalQty);
        ctx.getSubmit().setTotalEarning(totalEarning);
    }
}
