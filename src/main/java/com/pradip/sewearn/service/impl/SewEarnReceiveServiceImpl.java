package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.MarkAsCompletedRequest;
import com.pradip.sewearn.dto.receive.*;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.ReceivedItemMapper;
import com.pradip.sewearn.mapper.SewEarnReceiveMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.receive.*;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.repository.ReceivedItemRepository;
import com.pradip.sewearn.repository.SewEarnReceiveRepository;

import com.pradip.sewearn.service.SewEarnReceiveService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SewEarnReceiveServiceImpl implements SewEarnReceiveService {

    private final SewEarnReceiveRepository receiveRepository;
    private final ReceivedItemRepository receivedItemRepository;
    private final RawMaterialTypeRepository materialRepository;
    private final SewEarnReceiveMapper mapper;
    private final ReceivedItemMapper receivedItemMapper;


    // =====================================
    // CREATE
    // =====================================
    @Override
    public SewEarnReceiveResponse createReceive(SewEarnReceiveRequest request) {

        SewEarnReceive entity = mapper.toEntity(request);

        int totalQty = 0;
//        double totalEarning = 0.0;

        for (ReceivedItemRequest itemReq : request.getReceivedItems()) {

            RawMaterialType material = resolveMaterial(itemReq.getRawMaterialTypeId(), itemReq.getRawMaterialTypeName());

            ReceivedItem item = mapper.toReceivedItemEntity(itemReq);
            item.setRawMaterialType(material);

            entity.addReceivedItem(item);

            totalQty += item.getQuantity();
//            totalEarning += material.getPrice() * item.getQuantity();
        }

        entity.setTotalReceivedQuantity(totalQty);
//        entity.setTotalEarning(totalEarning);

        return mapper.toDto(receiveRepository.save(entity));
    }


    // =====================================
    // READ
    // =====================================
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


    // =====================================
    // UPDATE
    // =====================================
    @Override
    public SewEarnReceiveResponse updateReceive(Long id, SewEarnReceiveRequest request) {

        SewEarnReceive existing = receiveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receive not found with id: " + id));

        existing.getReceivedItems().clear();
        existing.setReceivedDate(request.getReceivedDate());

        int totalQty = 0;
//        double totalEarning = 0.0;

        for (ReceivedItemRequest itemReq : request.getReceivedItems()) {
            RawMaterialType material = resolveMaterial(itemReq.getRawMaterialTypeId(), itemReq.getRawMaterialTypeName());

            ReceivedItem item = mapper.toReceivedItemEntity(itemReq);
            item.setRawMaterialType(material);

            existing.addReceivedItem(item);

            totalQty += item.getQuantity();
//            totalEarning += material.getPrice() * item.getQuantity();
        }

        existing.setTotalReceivedQuantity(totalQty);
//        existing.setTotalEarning(totalEarning);

        return mapper.toDto(receiveRepository.save(existing));
    }


    // =====================================
    // DELETE
    // =====================================
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

    // =====================================
    // Helper: Resolve Material
    // =====================================
    private RawMaterialType resolveMaterial(Long id, String name) {
        if (id != null && id != 0) {
            return materialRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with ID: " + id));
        }
        if (name != null && !name.isBlank()) {
            return materialRepository.findByNameIgnoreCase(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with name: " + name));
        }
        throw new ResourceNotFoundException("Material id or name must be provided");
    }


    @Override
    public SewEarnReceiveResponse markAsCompleted(Long id, MarkAsCompletedRequest req) {

        SewEarnReceive receive = receiveRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Receive batch not found: " + id));

        if (receive.getMarkAsCompleted()) throw new IllegalStateException("Already marked as completed");

        receive.setMarkAsCompleted(true);

        LocalDate completedDate = (req.getCompletedDate() != null) ? req.getCompletedDate() : LocalDate.now();
        double totalEarnings = 0.0;

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
