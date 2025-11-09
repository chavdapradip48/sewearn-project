package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.SewingMaterialRequest;
import com.pradip.sewearn.dto.SewingMaterialResponse;
import com.pradip.sewearn.model.SewingMaterial;

public class SewingMaterialMapper {

    public static SewingMaterial toEntity(SewingMaterialRequest dto) {
        return SewingMaterial.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
    }

    public static SewingMaterialResponse toDto(SewingMaterial entity) {
        return SewingMaterialResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .build();
    }
}
