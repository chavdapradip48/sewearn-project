package com.pradip.sewearn.controller;

import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.WorkItemRequest;
import com.pradip.sewearn.dto.WorkItemResponse;
import com.pradip.sewearn.mapper.WorkItemMapper;
import com.pradip.sewearn.model.WorkItem;
import com.pradip.sewearn.service.WorkItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-items")
@RequiredArgsConstructor
@Tag(name = "Work Items", description = "APIs for managing sewing work submissions and earnings calculation")
public class WorkItemController {

    private final WorkItemService workItemService;

    @Operation(summary = "Create new work item",
            description = "Add a new sewing work entry. Material can be identified by ID or name. " +
                    "The total price will be calculated automatically based on the quantity and material price.")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkItemResponse>> createWorkItem(
            @Valid @RequestBody WorkItemRequest request) {

        WorkItem workItem = workItemService.createWorkItem(request);
        WorkItemResponse response = WorkItemMapper.toDto(workItem);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("Work item created successfully", HttpStatus.CREATED.value(), response));
    }

    @Operation(summary = "Get all work items",
            description = "Retrieve the complete list of all submitted sewing work items.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkItemResponse>>> getAllWorkItems() {
        List<WorkItemResponse> workItems = workItemService.getAllWorkItems()
                .stream()
                .map(WorkItemMapper::toDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.of("Fetched all work items", HttpStatus.OK.value(), workItems));
    }

    @Operation(summary = "Get work item by ID",
            description = "Retrieve a specific work item by its unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItemResponse>> getWorkItemById(@PathVariable Long id) {
        WorkItem workItem = workItemService.getWorkItemById(id);
        return ResponseEntity.ok(ApiResponse.of("Work item found", HttpStatus.OK.value(), WorkItemMapper.toDto(workItem)));
    }

    @Operation(summary = "Update work item",
            description = "Update an existing work item’s material or quantity. The total will be recalculated automatically.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItemResponse>> updateWorkItem(
            @PathVariable Long id,
            @Valid @RequestBody WorkItemRequest request) {

        WorkItem updated = workItemService.updateWorkItem(id, request);
        return ResponseEntity.ok(ApiResponse.of("Work item updated successfully", HttpStatus.OK.value(), WorkItemMapper.toDto(updated)));
    }

    @Operation(summary = "Delete work item",
            description = "Delete a work item permanently using its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorkItem(@PathVariable Long id) {
        workItemService.deleteWorkItem(id);
        return ResponseEntity.ok(ApiResponse.of("Work item deleted successfully", HttpStatus.OK.value(), null));
    }
}
