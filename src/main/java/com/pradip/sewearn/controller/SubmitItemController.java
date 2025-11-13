package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.submit.*;
import com.pradip.sewearn.service.SubmitItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submit-items")
@RequiredArgsConstructor
@Tag(name = "Submit Items", description = "Manage individual submitted items")
public class SubmitItemController {

    private final SubmitItemService service;

    @Operation(summary = "Create submit item (under a submission)")
    @PostMapping
    public ResponseEntity<ApiResponse<SubmitItemResponse>> create(
            @Valid @RequestBody SubmitItemCreateRequest request) {

        SubmitItemResponse dto = service.createSubmitItem(request);
        return ResponseEntity.status(201).body(ApiResponse.success(ApiMessages.SUBMIT_ITEM_CREATED, dto, 201));
    }

    @Operation(summary = "Get submit item by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmitItemResponse>> getById(@PathVariable Long id) {
        SubmitItemResponse dto = service.getSubmitItemById(id);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_ITEM_FETCHED, dto));
    }

    @Operation(summary = "List items for a submission")
    @GetMapping("/submission/{submitId}")
    public ResponseEntity<ApiResponse<List<SubmitItemResponse>>> listBySubmit(@PathVariable Long submitId) {
        List<SubmitItemResponse> list = service.getItemsBySubmitId(submitId);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_ITEM_LIST_FETCHED, list));
    }

    @Operation(summary = "Update submit item")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmitItemResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubmitItemUpdateRequest request) {

        SubmitItemResponse dto = service.updateSubmitItem(id, request);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_ITEM_UPDATED, dto));
    }

    @Operation(summary = "Delete submit item")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSubmitItem(id);
        return ResponseEntity.noContent().build();
    }
}
