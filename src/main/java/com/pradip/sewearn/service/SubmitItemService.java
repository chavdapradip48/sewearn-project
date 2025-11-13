package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.submit.*;

import java.util.List;

public interface SubmitItemService {

    SubmitItemResponse createSubmitItem(SubmitItemCreateRequest request);

    SubmitItemResponse getSubmitItemById(Long id);

    List<SubmitItemResponse> getItemsBySubmitId(Long submitId);

    SubmitItemResponse updateSubmitItem(Long id, SubmitItemUpdateRequest request);

    void deleteSubmitItem(Long id);
}
