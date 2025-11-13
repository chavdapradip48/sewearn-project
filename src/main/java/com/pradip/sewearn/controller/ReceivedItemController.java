package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.receive.*;
import com.pradip.sewearn.service.ReceivedItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/received-items")
@RequiredArgsConstructor
@Tag(name = "Received Items", description = "Manage received items (child of receive batches)")
public class ReceivedItemController {

    private final ReceivedItemService service;

    @Operation(summary = "Create received item (under a receive)")
    @PostMapping
    public ResponseEntity<ApiResponse<ReceivedItemResponse>> create(
            @Valid @RequestBody ReceivedItemCreateRequest request) {

        ReceivedItemResponse dto = service.createReceivedItem(request);
        return ResponseEntity.status(201).body(ApiResponse.success(ApiMessages.RECEIVED_ITEM_CREATED, dto, 201));
    }

    @Operation(summary = "Get received item by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReceivedItemResponse>> getById(@PathVariable Long id) {
        ReceivedItemResponse dto = service.getReceivedItemById(id);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVED_ITEM_FETCHED, dto));
    }

    @Operation(summary = "List items for a receive")
    @GetMapping("/receive/{receiveId}")
    public ResponseEntity<ApiResponse<List<ReceivedItemResponse>>> listByReceive(@PathVariable Long receiveId) {
        List<ReceivedItemResponse> list = service.getItemsByReceiveId(receiveId);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVED_ITEM_LIST_FETCHED, list));
    }

    @Operation(summary = "Update received item")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReceivedItemResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ReceivedItemUpdateRequest request) {

        ReceivedItemResponse dto = service.updateReceivedItem(id, request);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVED_ITEM_UPDATED, dto));
    }

    @Operation(summary = "Delete received item")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteReceivedItem(id);
        return ResponseEntity.noContent().build();
    }
}
