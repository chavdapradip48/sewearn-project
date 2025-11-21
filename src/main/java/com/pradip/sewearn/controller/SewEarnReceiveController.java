package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.MarkAsCompletedRequest;
import com.pradip.sewearn.dto.PagedResponse;
import com.pradip.sewearn.dto.receive.SewEarnReceiveRequest;
import com.pradip.sewearn.dto.receive.SewEarnReceiveResponse;
import com.pradip.sewearn.dto.receive.SewEarnReceiveSummaryResponse;
import com.pradip.sewearn.mapper.SewEarnReceiveMapper;
import com.pradip.sewearn.model.receive.SewEarnReceive;
import com.pradip.sewearn.service.SewEarnReceiveService;
import com.pradip.sewearn.util.PagingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/receives")
@RequiredArgsConstructor
@Tag(name = "Receives", description = "Manage received raw material batches")
public class SewEarnReceiveController {

    private final SewEarnReceiveService service;

    // CREATE
    @Operation(summary = "Create receive batch")
    @PostMapping
    public ResponseEntity<ApiResponse<SewEarnReceiveResponse>> create(
            @Valid @RequestBody SewEarnReceiveRequest request) {

        SewEarnReceiveResponse dto = service.createReceive(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(ApiMessages.RECEIVE_CREATED, dto, 201));
    }

    // FETCH BY ID
    @Operation(summary = "Get receive by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SewEarnReceiveResponse>> getById(@PathVariable Long id) {
        SewEarnReceiveResponse dto = service.getReceiveById(id);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVE_FETCHED, dto));
    }

    // PAGED LIST
    @Operation(summary = "Get all receives (paged)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<SewEarnReceiveResponse>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "receivedDate") String sort,
            @RequestParam(defaultValue = "DESC") String dir) {

        Page<SewEarnReceiveResponse> result = service.getAllReceives(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(dir), sort)));
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVE_LIST_FETCHED, PagingUtils.toPagedResponse(result)));
    }

    @Operation(summary = "Summary list of receives")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PagedResponse<SewEarnReceiveSummaryResponse>>> summaryList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "receivedDate") String sort,
            @RequestParam(defaultValue = "DESC") String dir
    ) {
        Page<SewEarnReceiveSummaryResponse> allReceivesSummaryPaged = service.getAllReceivesSummaryPaged(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(dir), sort)));
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVE_LIST_FETCHED, PagingUtils.toPagedResponse(allReceivesSummaryPaged)));
    }

    @Operation(summary = "Get receives by date (paged)")
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<PagedResponse<SewEarnReceiveSummaryResponse>>> getByDate(
            @PathVariable String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("receivedDate").descending());
        Page<SewEarnReceiveSummaryResponse> result =
                service.getReceivesByDateSummary(LocalDate.parse(date), pageable);

        PagedResponse<SewEarnReceiveSummaryResponse> paged = PagedResponse.<SewEarnReceiveSummaryResponse>builder()
                .items(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVE_LIST_FETCHED, paged));
    }

    // UPDATE
    @Operation(summary = "Update receive batch")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SewEarnReceiveResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SewEarnReceiveRequest request) {

        SewEarnReceiveResponse dto = service.updateReceive(id, request);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.RECEIVE_UPDATED, dto));
    }

    // DELETE
    @Operation(summary = "Delete receive batch")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteReceive(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mark receive batch as completed")
    @PostMapping("/{id}/mark-as-completed")
    public ResponseEntity<ApiResponse<SewEarnReceiveResponse>> markAsCompleted(
            @PathVariable Long id,
            @RequestBody(required = false) MarkAsCompletedRequest request) {

        SewEarnReceiveResponse updated = service.markAsCompleted(id, request);
        return ResponseEntity.ok(ApiResponse.success("Marked as completed", updated));
    }
}
