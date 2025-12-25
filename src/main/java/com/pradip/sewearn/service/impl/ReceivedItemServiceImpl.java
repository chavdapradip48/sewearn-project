package com.pradip.sewearn.service.impl;


import com.pradip.sewearn.dto.receive.ReceivedItemCreateRequest;
import com.pradip.sewearn.dto.receive.ReceivedItemResponse;
import com.pradip.sewearn.dto.receive.ReceivedItemUpdateRequest;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.ReceivedItemMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.receive.ReceivedItem;
import com.pradip.sewearn.model.receive.SewEarnReceive;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.repository.receive.ReceivedItemRepository;
import com.pradip.sewearn.repository.receive.SewEarnReceiveRepository;
import com.pradip.sewearn.service.ReceivedItemService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceivedItemServiceImpl implements ReceivedItemService {

    private final ReceivedItemRepository receivedItemRepository;
    private final SewEarnReceiveRepository receiveRepository;
    private final RawMaterialTypeRepository materialRepository;

    private final ReceivedItemMapper mapper;


    // =====================================
    // CREATE
    // =====================================
    @Override
    public ReceivedItemResponse createReceivedItem(ReceivedItemCreateRequest request) {

        SewEarnReceive receive = receiveRepository.findById(request.getReceiveId())
                .orElseThrow(() -> new ResourceNotFoundException("Receive not found with id: " + request.getReceiveId()));

        RawMaterialType material = resolveMaterial(request.getMaterialId(), request.getMaterialName());

        ReceivedItem item = mapper.toEntity(request);
        item.setRawMaterialType(material);

        receive.addReceivedItem(item);

        // update totals
        receive.setTotalReceivedQuantity(receive.getTotalReceivedQuantity() + item.getQuantity());
        receive.setTotalEarning(receive.getTotalEarning() + material.getPrice() * item.getQuantity());

        receivedItemRepository.save(item);

        return mapper.toDto(item);
    }


    // =====================================
    // READ
    // =====================================
    @Override
    @Transactional(readOnly = true)
    public ReceivedItemResponse getReceivedItemById(Long id) {
        ReceivedItem item = receivedItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReceivedItem not found with id: " + id));

        return mapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceivedItemResponse> getItemsByReceiveId(Long receiveId) {
        return receivedItemRepository.findByReceiveId(receiveId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }


    // =====================================
    // UPDATE
    // =====================================
    @Override
    public ReceivedItemResponse updateReceivedItem(Long id, ReceivedItemUpdateRequest request) {

        ReceivedItem existing = receivedItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReceivedItem not found with id: " + id));

        // Update material if required
        RawMaterialType material = resolveMaterial(request.getMaterialId(), request.getMaterialName());
        existing.setRawMaterialType(material);

        // Adjust total quantity & earnings in parent
        SewEarnReceive parent = existing.getReceive();
        parent.setTotalReceivedQuantity(
                parent.getTotalReceivedQuantity() - existing.getQuantity() + request.getQuantity()
        );
        parent.setTotalEarning(
                parent.getTotalEarning() - (existing.getRawMaterialType().getPrice() * existing.getQuantity()) +
                        (material.getPrice() * request.getQuantity())
        );

        mapper.updateEntity(existing, request);

        return mapper.toDto(receivedItemRepository.save(existing));
    }


    // =====================================
    // DELETE
    // =====================================
    @Override
    public void deleteReceivedItem(Long id) {

        ReceivedItem existing = receivedItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReceivedItem not found with id: " + id));

        SewEarnReceive parent = existing.getReceive();

        parent.setTotalReceivedQuantity(parent.getTotalReceivedQuantity() - existing.getQuantity());
        parent.setTotalEarning(
                parent.getTotalEarning() - existing.getRawMaterialType().getPrice() * existing.getQuantity()
        );

        receivedItemRepository.delete(existing);
    }


    // =====================================
    // Helper
    // =====================================
    private RawMaterialType resolveMaterial(Long id, String name) {
        if (id != null) {
            return materialRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));
        }
        if (name != null && !name.isBlank()) {
            return materialRepository.findByNameIgnoreCase(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with name: " + name));
        }
        throw new ResourceNotFoundException("Material id or name must be provided");
    }
}
