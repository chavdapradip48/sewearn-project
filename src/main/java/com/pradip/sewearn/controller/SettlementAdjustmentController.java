package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.payment.SettlementAdjustmentResponse;
import com.pradip.sewearn.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements/{settlementId}/adjustments")
@RequiredArgsConstructor
@Tag(name = "Settlement Adjustments", description = "Manage settlement expenses and extras")
public class SettlementAdjustmentController {

    private final SettlementService service;

    @Operation(summary = "List adjustments for settlement")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SettlementAdjustmentResponse>>> listAdjustments(
            @PathVariable Long settlementId) {

        List<SettlementAdjustmentResponse> list =
                service.getById(settlementId).getAdjustments();

        return ResponseEntity.ok(
                ApiResponse.success(ApiMessages.SETTLEMENT_ADJUSTMENT_LIST_FETCHED, list)
        );
    }
}
