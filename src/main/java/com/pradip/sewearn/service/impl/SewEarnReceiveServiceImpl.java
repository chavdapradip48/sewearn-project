package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.MarkAsCompletedRequest;
import com.pradip.sewearn.dto.receive.*;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.ReceivedItemMapper;
import com.pradip.sewearn.mapper.SewEarnReceiveMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.receive.ItemTrack;
import com.pradip.sewearn.model.receive.ReceivedItem;
import com.pradip.sewearn.model.receive.SewEarnReceive;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.repository.SewEarnReceiveRepository;
import com.pradip.sewearn.service.RawMaterialTypeService;
import com.pradip.sewearn.service.SewEarnReceiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SewEarnReceiveServiceImpl implements SewEarnReceiveService {

    private final SewEarnReceiveRepository receiveRepository;
    private final RawMaterialTypeService materialTypeService;
    private final SewEarnReceiveMapper mapper;
    private final ReceivedItemMapper receivedItemMapper;

    @Override
    public SewEarnReceiveResponse createReceive(SewEarnReceiveRequest request) {
        SewEarnReceive entity = mapper.toEntity(request);
        int totalQty = 0;

        for (ReceivedItemRequest itemReq : request.getReceivedItems()) {
            RawMaterialType material = materialTypeService.getMaterialByIdOrName(itemReq.getRawMaterialTypeId(), itemReq.getRawMaterialTypeName());
            ReceivedItem item = mapper.toReceivedItemEntity(itemReq);
            item.setRawMaterialType(material);
            entity.addReceivedItem(item);
            totalQty += item.getQuantity();
        }

        entity.setTotalReceivedQuantity(totalQty);
        return mapper.toDto(receiveRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SewEarnReceiveResponse getReceiveById(Long id) {
        return mapper.toDto(
                receiveRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Receive not found with id: " + id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnReceiveResponse> getAllReceives(Pageable pageable) {
        return receiveRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnReceiveResponse> getReceivesByDate(LocalDate date, Pageable pageable) {
        return receiveRepository.findByReceivedDate(date, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnReceiveResponse> getReceivesByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return receiveRepository.findByReceivedDateBetween(start, end, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public SewEarnReceiveResponse updateReceive(Long id, SewEarnReceiveUpdateRequest request) {

        SewEarnReceive existing = receiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receive not found with id: " + id));
        existing.setReceivedDate(request.getReceivedDate());

        // Map of existing ReceivedItems for quick lookup
        Map<Long, ReceivedItem> existingMap = existing.getReceivedItems().stream()
                .collect(Collectors.toMap(ReceivedItem::getId, ri -> ri));

        List<ReceivedItem> finalList = new ArrayList<>();
        int totalQty = 0;
        Long totalEarnings = 0L;

        for (SWReceivedItemUpdateRequest itemReq : request.getReceivedItems()) {

            RawMaterialType material = materialTypeService.getMaterialByIdOrName(itemReq.getMaterialId(), "");
            ReceivedItem updatedItem;

            // CASE 1: UPDATE existing ReceivedItem — do NOT delete ItemTracks
            if (itemReq.getId() != null && existingMap.containsKey(itemReq.getId())) {
                updatedItem = existingMap.get(itemReq.getId());
                updatedItem.setQuantity(itemReq.getQuantity());
                updatedItem.setRawMaterialType(material);
                existingMap.remove(itemReq.getId());
            } else {

                // CASE 2: NEW ReceivedItem — create fresh
                updatedItem = mapper.toReceivedItemEntity(itemReq);
                updatedItem.setRawMaterialType(material);
                updatedItem.setReceive(existing);
            }

            finalList.add(updatedItem);
            totalQty += updatedItem.getQuantity();
            totalEarnings += (updatedItem.getRawMaterialType().getPrice() * updatedItem.getTotalCompletedQuantity());
        }

        // CASE 3: DELETE those items that are NOT present in request
        for (ReceivedItem toRemove : existingMap.values()) {
            existing.removeReceivedItem(toRemove);
        }

        existing.setReceivedItems(finalList);
        existing.setTotalReceivedQuantity(totalQty);
        existing.setTotalEarning(totalEarnings);

        SewEarnReceive saved = receiveRepository.save(existing);

        return mapper.toDto(saved);
    }

    @Override
    public void deleteReceive(Long id) {
        SewEarnReceive existing = receiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receive not found with id: " + id));

        receiveRepository.delete(existing);
    }

    @Override
    public List<SewEarnReceiveSummaryResponse> getAllReceivesSummary() {
        return receiveRepository.findAll().stream().map(mapper::toSummary).toList();
    }

    @Override
    public Page<SewEarnReceiveSummaryResponse> getAllReceivesSummaryPaged(Pageable pageable) {
        return receiveRepository.findAll(pageable).map(mapper::toSummary);
    }

    @Override
    public Page<SewEarnReceiveSummaryResponse> getReceivesByDateSummary(LocalDate date, Pageable pageable) {
        return receiveRepository.findByReceivedDate(date, pageable)
                .map(mapper::toSummary);
    }

    @Override
    public SewEarnReceiveResponse markAsCompleted(Long id, MarkAsCompletedRequest req) {

        SewEarnReceive receive = receiveRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Receive batch not found: " + id));

        if (receive.getMarkAsCompleted()) throw new IllegalStateException("Already marked as completed");

        receive.setMarkAsCompleted(true);

        LocalDate completedDate = (req.getCompletedDate() != null) ? req.getCompletedDate() : LocalDate.now();
        long totalEarnings = 0L;

        for (ReceivedItem item : receive.getReceivedItems()) {
            Integer completedQty = item.getQuantity();  // Full completion
            item.setTotalCompletedQuantity(completedQty);
            item.addItemTrack(ItemTrack.builder().completedDate(completedDate).completedQuantity(completedQty)
                    .receivedItem(item).build());
            totalEarnings += completedQty * item.getRawMaterialType().getPrice();
        }
        receive.setTotalEarning(totalEarnings);
        return mapper.toDto(receiveRepository.save(receive));
    }

    public Page<SewEarnReceiveDto> getProgress(boolean completed, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SewEarnReceive> pageResult = receiveRepository.findAllByCompleted(completed, pageable);

        return pageResult.map(receivedItemMapper::toReceiveDto);
    }

}
