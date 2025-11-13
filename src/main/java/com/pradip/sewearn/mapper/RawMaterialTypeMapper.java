package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.RawMaterialTypeRequest;
import com.pradip.sewearn.dto.RawMaterialTypeResponse;
import com.pradip.sewearn.model.RawMaterialType;
import org.springframework.stereotype.Component;

@Component
public class RawMaterialTypeMapper {

    public RawMaterialType toEntity(RawMaterialTypeRequest dto) {
        return RawMaterialType.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
    }

    public RawMaterialTypeResponse toDto(RawMaterialType entity) {
        return RawMaterialTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .build();
    }

    public void updateEntity(RawMaterialType entity, RawMaterialTypeRequest dto) {
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
    }
}