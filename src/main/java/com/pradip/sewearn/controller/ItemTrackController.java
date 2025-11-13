package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.receive.ItemTrackRequest;
import com.pradip.sewearn.dto.receive.ItemTrackResponse;
import com.pradip.sewearn.service.ItemTrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/item-tracks")
@RequiredArgsConstructor
@Tag(name = "Item Tracks", description = "Daily completion tracking for received items")
public class ItemTrackController {

    private final ItemTrackService service;

    @Operation(summary = "Add item track for a received item")
    @PostMapping("/received-item/{receivedItemId}")
    public ResponseEntity<ApiResponse<ItemTrackResponse>> addTrack(
            @PathVariable Long receivedItemId,
            @Valid @RequestBody ItemTrackRequest request) {

        ItemTrackResponse dto = service.addItemTrack(receivedItemId, request);
        return ResponseEntity.status(201).body(ApiResponse.success(ApiMessages.ITEM_TRACK_CREATED, dto, 201));
    }

    @Operation(summary = "Get track by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemTrackResponse>> getById(@PathVariable Long id) {
        ItemTrackResponse dto = service.getItemTrackById(id);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.ITEM_TRACK_FETCHED, dto));
    }

    @Operation(summary = "List tracks by received item")
    @GetMapping("/received-item/{receivedItemId}")
    public ResponseEntity<ApiResponse<List<ItemTrackResponse>>> listByReceivedItem(@PathVariable Long receivedItemId) {
        List<ItemTrackResponse> list = service.getTracksByReceivedItem(receivedItemId);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.ITEM_TRACK_LIST_FETCHED, list));
    }

    @Operation(summary = "List tracks by date")
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<ItemTrackResponse>>> listByDate(@PathVariable String date) {
        List<ItemTrackResponse> list = service.getTracksByDate(LocalDate.parse(date));
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.ITEM_TRACK_LIST_FETCHED, list));
    }

    @Operation(summary = "Update item track")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemTrackResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemTrackRequest request) {

        ItemTrackResponse dto = service.updateItemTrack(id, request);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.ITEM_TRACK_UPDATED, dto));
    }

    @Operation(summary = "Delete item track")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteItemTrack(id);
        return ResponseEntity.noContent().build();
    }
}
