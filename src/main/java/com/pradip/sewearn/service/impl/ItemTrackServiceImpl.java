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

    @Override
    public ItemTrackResponse updateItemTrack(Long id, ItemTrackRequest request) {

        ItemTrack existingTrack = itemTrackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ItemTrack not found with id: " + id));

        ReceivedItem receivedItem = existingTrack.getReceivedItem();

        int oldQty = existingTrack.getCompletedQuantity();  // IMPORTANT
        int newQty = request.getCompletedQuantity();

        int totalWithoutCurrentTrack = receivedItem.getTotalCompletedQuantity() - oldQty;

        if (receivedItem.getQuantity() < totalWithoutCurrentTrack + newQty) {
            int remain = receivedItem.getQuantity() - totalWithoutCurrentTrack;
            throw new BadRequestException("Only " + remain + " quantity can be completed for "
                    + receivedItem.getRawMaterialType().getName());
        }

        mapper.updateEntity(existingTrack, request);
        ItemTrack savedTrack = itemTrackRepository.save(existingTrack);

        if (oldQty == newQty) {
            return mapper.toDto(savedTrack);
        }

        receivedItem.setTotalCompletedQuantity(totalWithoutCurrentTrack + newQty);
        receivedItemRepository.save(receivedItem);

        updateParent(receivedItem, oldQty, newQty);

        return mapper.toDto(savedTrack);
    }

    private void updateParent(ReceivedItem receivedItem, int oldQty, int newQty) {

        SewEarnReceive parent = receivedItem.getReceive();
        long price = receivedItem.getRawMaterialType().getPrice();
        long oldEarnings = oldQty * price;
        long newEarnings = newQty * price;

        parent.setTotalEarning(parent.getTotalEarning() - oldEarnings + newEarnings);
        long pendingCount = receivedItemRepository.countPendingItems(parent.getId());

        parent.setMarkAsCompleted(pendingCount == 0);

        sewEarnReceiveRepository.save(parent);
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
    public void deleteItemTrack(Long id) {

        ItemTrack track = itemTrackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ItemTrack not found with id: " + id));

        ReceivedItem receivedItem = track.getReceivedItem();
        SewEarnReceive parent = receivedItem.getReceive();

        int deletedQty = track.getCompletedQuantity();
        long price = receivedItem.getRawMaterialType().getPrice();

        itemTrackRepository.delete(track);

        receivedItem.setTotalCompletedQuantity(safeInt(receivedItem.getTotalCompletedQuantity()) - deletedQty);
        receivedItemRepository.save(receivedItem);

        updateParentAfterDelete(parent, receivedItem, deletedQty * price);
    }

    private void updateParentAfterDelete(SewEarnReceive parent, ReceivedItem receivedItem, long deletedEarnings) {
        parent.setTotalEarning(parent.getTotalEarning() - deletedEarnings);
        parent.setMarkAsCompleted(false);

        sewEarnReceiveRepository.save(parent);
    }


    // defensive helper
    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }
}
