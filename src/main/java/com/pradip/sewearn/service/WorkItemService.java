package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.WorkItemRequest;
import com.pradip.sewearn.model.WorkItem;

import java.util.List;

public interface WorkItemService {
    WorkItem createWorkItem(WorkItemRequest request);
    WorkItem getWorkItemById(Long id);
    List<WorkItem> getAllWorkItems();
    WorkItem updateWorkItem(Long id, WorkItemRequest request);
    void deleteWorkItem(Long id);
}