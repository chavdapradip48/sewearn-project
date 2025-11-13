package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.receive.*;
import com.pradip.sewearn.model.receive.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReceivedItemMapper {

    // ==========================
    // Entity -> DTO
    // ==========================
    public ReceivedItemResponse toDto(ReceivedItem item) {
        if (item == null) return null;

        return ReceivedItemResponse.builder()
                .id(item.getId())
                .materialName(item.getRawMaterialType().getName())
                .quantity(item.getQuantity())
                .totalCompletedQuantity(item.getTotalCompletedQuantity())
                .itemTracks(
                        item.getItemTracks().stream()
                                .map(track -> ItemTrackResponse.builder()
                                        .id(track.getId())
                                        .completedDate(track.getCompletedDate())
                                        .completedQuantity(track.getCompletedQuantity())
                                        .build()
                                ).collect(Collectors.toList())
                )
                .build();
    }

    // ==========================
    // DTO -> Entity
    // ==========================
    public ReceivedItem toEntity(ReceivedItemCreateRequest dto) {
        return ReceivedItem.builder()
                .quantity(dto.getQuantity())
                .totalCompletedQuantity(0)
                .build();
    }

    public void updateEntity(ReceivedItem item, ReceivedItemUpdateRequest dto) {
        item.setQuantity(dto.getQuantity());
    }
}
