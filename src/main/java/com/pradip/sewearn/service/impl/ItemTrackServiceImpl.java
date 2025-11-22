package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.receive.ItemTrackRequest;
import com.pradip.sewearn.dto.receive.ItemTrackResponse;
import com.pradip.sewearn.exception.custom.BadRequestException;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.ItemTrackMapper;
import com.pradip.sewearn.model.receive.ItemTrack;
import com.pradip.sewearn.model.receive.ReceivedItem;
import com.pradip.sewearn.model.receive.SewEarnReceive;
import com.pradip.sewearn.repository.ItemTrackRepository;
import com.pradip.sewearn.repository.ReceivedItemRepository;
import com.pradip.sewearn.repository.SewEarnReceiveRepository;
import com.pradip.sewearn.service.ItemTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemTrackServiceImpl implements ItemTrackService {

    private final ItemTrackRepository itemTrackRepository;
    private final ReceivedItemRepository receivedItemRepository;
    private final SewEarnReceiveRepository sewEarnReceiveRepository;
    private final ItemTrackMapper mapper;

    @Override
    public ItemTrackResponse addItemTrack(Long receivedItemId, ItemTrackRequest request) {
        ReceivedItem receivedItem = receivedItemRepository.findById(receivedItemId)
                .orElseThrow(() -> new ResourceNotFoundException("ReceivedItem not found with id: " + receivedItemId));

        validateItemTracReuqest(request, receivedItem);

        ItemTrack track = mapper.toEntity(request);
        track.setReceivedItem(receivedItem);

        ItemTrack saved = itemTrackRepository.save(track);

        int updatedTotal = safeInt(receivedItem.getTotalCompletedQuantity()) + saved.getCompletedQuantity();
        receivedItem.setTotalCompletedQuantity(updatedTotal);

        receivedItemRepository.save(receivedItem);
        SewEarnReceive sewEarnReceive = receivedItem.getReceive();

        long pendingCount = receivedItemRepository.countPendingItems(sewEarnReceive.getId());
        long todayCompletedEarnings = track.getCompletedQuantity() * receivedItem.getRawMaterialType().getPrice();

        if (pendingCount == 0) sewEarnReceive.setMarkAsCompleted(true);

        sewEarnReceive.setTotalEarning(sewEarnReceive.getTotalEarning() + todayCompletedEarnings);
        sewEarnReceiveRepository.save(sewEarnReceive);

        return mapper.toDto(saved);
    }

    private static void validateItemTracReuqest(ItemTrackRequest request, ReceivedItem receivedItem) {
        String materialName = receivedItem.getRawMaterialType().getName();

        if (Objects.equals(receivedItem.getQuantity(), receivedItem.getTotalCompletedQuantity())) throw  new BadRequestException(materialName + "material is already completed.");
        if (receivedItem.getQuantity() < receivedItem.getTotalCompletedQuantity() + request.getCompletedQuantity()) throw  new BadRequestException("Only " + (receivedItem.getQuantity() - receivedItem.getTotalCompletedQuantity()) + " quantity can be completed for " + materialName + ".");
    }

    @Override
    @Transactional(readOnly = true)
    public ItemTrackResponse getItemTrackById(Long id) {
        ItemTrack track = itemTrackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemTrack not found with id: " + id));
        return mapper.toDto(track);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemTrackResponse> getTracksByReceivedItem(Long receivedItemId) {
        List<ItemTrack> tracks = itemTrackRepository.findByReceivedItemId(receivedItemId);
        return tracks.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemTrackResponse> getTracksByDate(LocalDate date) {
        List<ItemTrack> tracks = itemTrackRepository.findByCompletedDate(date);
        return tracks.stream().map(mapper::toDto).toList();
    }

    @Override
    public ItemTrackResponse updateItemTrack(Long id, ItemTrackRequest request) {
        ItemTrack existing = itemTrackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemTrack not found with id: " + id));

        ReceivedItem parent = existing.getReceivedItem();
        validateItemTracReuqest(request, parent);
        int oldQty = existing.getCompletedQuantity();

        // update entity
        mapper.updateEntity(existing, request);
        ItemTrack saved = itemTrackRepository.save(existing);

        // update parent's aggregate by delta
        int delta = saved.getCompletedQuantity() - oldQty;
        parent.setTotalCompletedQuantity(safeInt(parent.getTotalCompletedQuantity()) + delta);
        receivedItemRepository.save(parent);

        return mapper.toDto(saved);
    }

    @Override
    public void deleteItemTrack(Long id) {
        ItemTrack existing = itemTrackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemTrack not found with id: " + id));

        ReceivedItem parent = existing.getReceivedItem();
        int qty = existing.getCompletedQuantity();

        itemTrackRepository.delete(existing);

        if (parent != null) {
            parent.setTotalCompletedQuantity(safeInt(parent.getTotalCompletedQuantity()) - qty);
            receivedItemRepository.save(parent);
        }
    }

    // defensive helper
    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }
}
