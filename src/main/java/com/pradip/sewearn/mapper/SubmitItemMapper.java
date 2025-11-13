package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.submit.*;
import com.pradip.sewearn.model.submit.SubmitItem;
import org.springframework.stereotype.Component;

@Component
public class SubmitItemMapper {

    public SubmitItem toEntity(SubmitItemCreateRequest dto) {
        return SubmitItem.builder()
                .quantity(dto.getQuantity())
                .build();
    }

    public void updateEntity(SubmitItem entity, SubmitItemUpdateRequest dto) {
        entity.setQuantity(dto.getQuantity());
    }

    public SubmitItemResponse toDto(SubmitItem entity) {
        return SubmitItemResponse.builder()
                .id(entity.getId())
                .materialName(entity.getRawMaterialType().getName())
                .quantity(entity.getQuantity())
                .totalEarning(entity.getTotalEarning())
                .submitId(entity.getSubmit().getId())
                .build();
    }
}
