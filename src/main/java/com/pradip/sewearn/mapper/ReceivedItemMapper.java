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

    public ReceivedItemDto toItemDto(ReceivedItem ri) {
        return ReceivedItemDto.builder()
                .id(ri.getId())
                .materialTypeId(ri.getRawMaterialType().getId())
                .materialName(ri.getRawMaterialType().getName())
                .quantity(ri.getQuantity())
                .completedQuantity(ri.getTotalCompletedQuantity())
                .pendingQuantity(ri.getQuantity() - ri.getTotalCompletedQuantity())
                .build();
    }

    public SewEarnReceiveDto toReceiveDto(SewEarnReceive r) {
        return SewEarnReceiveDto.builder()
                .id(r.getId())
                .receivedDate(r.getReceivedDate())
                .totalReceivedQuantity(r.getTotalReceivedQuantity())
                .totalEarning(r.getTotalEarning())
                .markAsCompleted(r.getMarkAsCompleted())
                .items(r.getReceivedItems().stream().map(this::toItemDto).collect(Collectors.toList()))
                .build();
    }
}
