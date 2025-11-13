package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.receive.*;
import com.pradip.sewearn.model.receive.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SewEarnReceiveMapper {

    // =====================================
    // Entity → DTO
    // =====================================
    public SewEarnReceiveResponse toDto(SewEarnReceive entity) {
        if (entity == null) return null;

        return SewEarnReceiveResponse.builder()
                .id(entity.getId())
                .receivedDate(entity.getReceivedDate())
                .totalReceivedQuantity(entity.getTotalReceivedQuantity())
                .totalEarning(entity.getTotalEarning())
                .receivedItems(
                        entity.getReceivedItems().stream()
                                .map(this::toItemDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private ReceivedItemResponse toItemDto(ReceivedItem item) {
        return ReceivedItemResponse.builder()
                .id(item.getId())
                .materialName(item.getRawMaterialType().getName())
                .quantity(item.getQuantity())
                .totalCompletedQuantity(item.getTotalCompletedQuantity())
                .itemTracks(
                        item.getItemTracks().stream()
                                .map(this::toTrackDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private ItemTrackResponse toTrackDto(ItemTrack track) {
        return ItemTrackResponse.builder()
                .id(track.getId())
                .completedDate(track.getCompletedDate())
                .completedQuantity(track.getCompletedQuantity())
                .build();
    }


    // =====================================
    // DTO → Entity
    // =====================================
    public SewEarnReceive toEntity(SewEarnReceiveRequest dto) {
        return SewEarnReceive.builder()
                .receivedDate(dto.getReceivedDate())
                .totalEarning(0.0)
                .totalReceivedQuantity(0)
                .build();
    }

    public ReceivedItem toReceivedItemEntity(ReceivedItemRequest dto) {
        return ReceivedItem.builder()
                .quantity(dto.getQuantity())
                .totalCompletedQuantity(0)
                .build();
    }

    public SewEarnReceiveSummaryResponse toSummary(SewEarnReceive entity) {
        return SewEarnReceiveSummaryResponse.builder()
                .id(entity.getId())
                .receivedDate(entity.getReceivedDate())
                .totalReceivedQuantity(entity.getTotalReceivedQuantity())
                .totalEarning(entity.getTotalEarning())
                .build();
    }
}
