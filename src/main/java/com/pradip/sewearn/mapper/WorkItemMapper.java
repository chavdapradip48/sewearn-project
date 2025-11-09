package com.pradip.sewearn.mapper;


import com.pradip.sewearn.dto.WorkItemRequest;
import com.pradip.sewearn.dto.WorkItemResponse;
import com.pradip.sewearn.model.SewingMaterial;
import com.pradip.sewearn.model.WorkItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkItemMapper {

    /**
     * Converts a WorkItem entity to a WorkItemResponse DTO.
     */
    public static WorkItemResponse toDto(WorkItem workItem) {
        if (workItem == null) return null;

        return WorkItemResponse.builder()
                .id(workItem.getId())
                .materialName(workItem.getMaterial() != null ? workItem.getMaterial().getName() : null)
                .quantity(workItem.getQuantity())
                .unitPrice(workItem.getMaterial() != null ? BigDecimal.valueOf(workItem.getMaterial().getPrice()) : null)
                .totalPrice(BigDecimal.valueOf(workItem.getTotalPrice()))
                .build();
    }

    /**
     * Converts a WorkItemRequest DTO to a WorkItem entity (partial mapping).
     * The SewingMaterial reference will be set in the service layer.
     */
    public static WorkItem toEntity(WorkItemRequest request, SewingMaterial material) {
        if (request == null) return null;

        WorkItem item = new WorkItem();
        item.setMaterial(material);
        item.setQuantity(request.getQuantity());
        item.setTotalPrice(material.getPrice() * request.getQuantity());

        return item;
    }
}