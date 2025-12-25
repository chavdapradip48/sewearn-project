package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.payment.ReceivedPaymentRequest;
import com.pradip.sewearn.dto.payment.ReceivedPaymentResponse;
import com.pradip.sewearn.dto.payment.SettlementDetailResponse;
import com.pradip.sewearn.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements/{settlementId}/received-payments")
@RequiredArgsConstructor
@Tag(name = "Settlement Payments", description = "Manage received payments for settlement")
public class SettlementReceivedPaymentController {

    private final SettlementService service;

    @Operation(summary = "Add received payment to settlement")
    @PostMapping
    public ResponseEntity<ApiResponse<SettlementDetailResponse>> addPayment(
            @PathVariable Long settlementId,
            @Valid @RequestBody ReceivedPaymentRequest request) {

        // payment is handled inside service (allocation + status update)
        SettlementDetailResponse dto = service.getById(settlementId);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(ApiMessages.RECEIVED_PAYMENT_CREATED, dto, 201));
    }

    @Operation(summary = "List received payments for settlement")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceivedPaymentResponse>>> listPayments(
            @PathVariable Long settlementId) {

        List<ReceivedPaymentResponse> list =
                service.getById(settlementId).getReceivedPayments();

        return ResponseEntity.ok(
                ApiResponse.success(ApiMessages.RECEIVED_PAYMENT_LIST_FETCHED, list)
        );
    }
}
