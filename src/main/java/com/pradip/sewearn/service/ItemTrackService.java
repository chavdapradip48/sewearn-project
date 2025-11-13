package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.receive.ItemTrackRequest;
import com.pradip.sewearn.dto.receive.ItemTrackResponse;

import java.time.LocalDate;
import java.util.List;

public interface ItemTrackService {

    ItemTrackResponse addItemTrack(Long receivedItemId, ItemTrackRequest request);

    ItemTrackResponse getItemTrackById(Long id);

    List<ItemTrackResponse> getTracksByReceivedItem(Long receivedItemId);

    List<ItemTrackResponse> getTracksByDate(LocalDate date);

    ItemTrackResponse updateItemTrack(Long id, ItemTrackRequest request);

    void deleteItemTrack(Long id);
}
