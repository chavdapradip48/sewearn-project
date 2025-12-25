package com.pradip.sewearn.controller;


import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.payment.*;
import com.pradip.sewearn.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlements", description = "Manage settlements and payments")
public class SettlementController {

    private final SettlementService service;

    /* =========================
       CALCULATE (PREVIEW)
       ========================= */

    @Operation(summary = "Calculate settlement for date range (preview only)")
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<SettlementCalculateResponse>> calculate(
            @Valid @RequestBody SettlementCalculateRequest request) {

        SettlementCalculateResponse dto = service.calculate(request);
        return ResponseEntity.ok(
                ApiResponse.success(ApiMessages.SETTLEMENT_CALCULATED, dto)
        );
    }

    /* =========================
       CREATE SETTLEMENT
       ========================= */

    @Operation(summary = "Create settlement")
    @PostMapping
    public ResponseEntity<ApiResponse<SettlementResponse>> create(
            @Valid @RequestBody SettlementCreateRequest request) {

        SettlementResponse dto = service.create(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(ApiMessages.SETTLEMENT_CREATED, dto, 201));
    }

    /* =========================
       UPDATE SETTLEMENT
       ========================= */

    @Operation(summary = "Update settlement")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SettlementResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SettlementUpdateRequest request) {

        SettlementResponse dto = service.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(ApiMessages.SETTLEMENT_UPDATED, dto)
        );
    }

    /* =========================
       GET SETTLEMENT BY ID
       ========================= */

    @Operation(summary = "Get settlement by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SettlementDetailResponse>> getById(
            @PathVariable Long id) {

        SettlementDetailResponse dto = service.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success(ApiMessages.SETTLEMENT_FETCHED, dto)
        );
    }

    /* =========================
       SETTLEMENT SUMMARY LIST
       ========================= */

    @Operation(summary = "List settlement summary")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<SettlementSummaryResponse>>> summaryList() {

        List<SettlementSummaryResponse> list = service.getSummaryList();
        return ResponseEntity.ok(
                ApiResponse.success(ApiMessages.SETTLEMENT_LIST_FETCHED, list)
        );
    }
}
