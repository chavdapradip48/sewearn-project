package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.receive.ItemTrackRequest;
import com.pradip.sewearn.dto.receive.ItemTrackResponse;
import com.pradip.sewearn.model.receive.ItemTrack;
import org.springframework.stereotype.Component;

@Component
public class ItemTrackMapper {

    public ItemTrack toEntity(ItemTrackRequest dto) {
        if (dto == null) return null;
        return ItemTrack.builder()
                .completedDate(dto.getCompletedDate())
                .completedQuantity(dto.getCompletedQuantity())
                .build();
    }

    public ItemTrackResponse toDto(ItemTrack entity) {
        if (entity == null) return null;
        return ItemTrackResponse.builder()
                .id(entity.getId())
                .completedDate(entity.getCompletedDate())
                .completedQuantity(entity.getCompletedQuantity())
                .receivedItemId(entity.getReceivedItem() != null ? entity.getReceivedItem().getId() : null)
                .build();
    }

    public void updateEntity(ItemTrack entity, ItemTrackRequest dto) {
        entity.setCompletedDate(dto.getCompletedDate());
        entity.setCompletedQuantity(dto.getCompletedQuantity());
    }
}
