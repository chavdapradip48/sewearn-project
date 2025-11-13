package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.receive.*;

import java.util.List;

public interface ReceivedItemService {

    ReceivedItemResponse createReceivedItem(ReceivedItemCreateRequest request);

    ReceivedItemResponse getReceivedItemById(Long id);

    List<ReceivedItemResponse> getItemsByReceiveId(Long receiveId);

    ReceivedItemResponse updateReceivedItem(Long id, ReceivedItemUpdateRequest request);

    void deleteReceivedItem(Long id);
}
