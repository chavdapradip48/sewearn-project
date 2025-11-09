package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.WorkItemRequest;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.model.SewingMaterial;
import com.pradip.sewearn.model.WorkItem;
import com.pradip.sewearn.repository.SewingMaterialRepository;
import com.pradip.sewearn.repository.WorkItemRepository;
import com.pradip.sewearn.service.WorkItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkItemServiceImpl implements WorkItemService {

    private final WorkItemRepository workItemRepository;
    private final SewingMaterialRepository sewingMaterialRepository;

    @Override
    public WorkItem createWorkItem(WorkItemRequest request) {
        SewingMaterial material = getMaterial(request);
        double total = material.getPrice() * request.getQuantity();

        WorkItem workItem = WorkItem.builder()
                .material(material)
                .quantity(request.getQuantity())
                .totalPrice(total)
                .build();

        return workItemRepository.save(workItem);
    }

    @Override
    public WorkItem getWorkItemById(Long id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkItem not found with ID: " + id));
    }

    @Override
    public List<WorkItem> getAllWorkItems() {
        return workItemRepository.findAll();
    }

    @Override
    public WorkItem updateWorkItem(Long id, WorkItemRequest request) {
        WorkItem existing = getWorkItemById(id);
        SewingMaterial material = getMaterial(request);
        double total = material.getPrice() * request.getQuantity();

        existing.setMaterial(material);
        existing.setQuantity(request.getQuantity());
        existing.setTotalPrice(total);

        return workItemRepository.save(existing);
    }

    @Override
    public void deleteWorkItem(Long id) {
        WorkItem existing = getWorkItemById(id);
        workItemRepository.delete(existing);
    }

    private SewingMaterial getMaterial(WorkItemRequest request) {
        if (request.getMaterialId() != null) {
            return sewingMaterialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with ID: " + request.getMaterialId()));
        } else if (request.getMaterialName() != null) {
            return sewingMaterialRepository.findByNameIgnoreCase(request.getMaterialName())
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with name: " + request.getMaterialName()));
        } else {
            throw new IllegalArgumentException("Either materialId or materialName must be provided");
        }
    }
}