package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.PagedResponse;
import com.pradip.sewearn.dto.submit.AwaitingSubmissionMaterialResponse;
import com.pradip.sewearn.dto.submit.SewEarnSubmitRequest;
import com.pradip.sewearn.dto.submit.SewEarnSubmitResponse;
import com.pradip.sewearn.dto.submit.SewEarnSubmitSummaryResponse;
import com.pradip.sewearn.service.SewEarnSubmitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Manage completed submissions and earnings")
public class SewEarnSubmitController {

    private final SewEarnSubmitService service;

    // CREATE
    @Operation(summary = "Create submission")
    @PostMapping
    public ResponseEntity<ApiResponse<SewEarnSubmitResponse>> create(
            @Valid @RequestBody SewEarnSubmitRequest request) {

        SewEarnSubmitResponse dto = service.createSubmission(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(ApiMessages.SUBMIT_CREATED, dto, 201));
    }

    // GET BY ID
    @Operation(summary = "Get submission by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SewEarnSubmitResponse>> getById(@PathVariable Long id) {
        SewEarnSubmitResponse dto = service.getSubmissionById(id);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_FETCHED, dto));
    }

    // PAGED LIST
    @Operation(summary = "Get all submissions (paged)")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<SewEarnSubmitSummaryResponse>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submissionDate") String sort,
            @RequestParam(defaultValue = "DESC") String dir) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(dir), sort)
        );

        Page<SewEarnSubmitSummaryResponse> result = service.getAllSubmissionsSummaryPaged(pageable);

        PagedResponse<SewEarnSubmitSummaryResponse> paged = PagedResponse.<SewEarnSubmitSummaryResponse>builder()
                .items(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_LIST_FETCHED, paged));
    }

    // SUMMARY LIST (NO PAGINATION)
    @Operation(summary = "Summary list of submissions")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<SewEarnSubmitSummaryResponse>>> summaryList() {
        List<SewEarnSubmitSummaryResponse> list = service.getAllSubmissionsSummary();
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_LIST_FETCHED, list));
    }

    // GET BY DATE (PAGED)
    @Operation(summary = "Get submissions by date (paged)")
    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<PagedResponse<SewEarnSubmitSummaryResponse>>> getByDate(
            @PathVariable String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("submissionDate").descending());
        Page<SewEarnSubmitSummaryResponse> result =
                service.getSubmissionsByDateSummary(LocalDate.parse(date), pageable);

        PagedResponse<SewEarnSubmitSummaryResponse> paged = PagedResponse.<SewEarnSubmitSummaryResponse>builder()
                .items(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_LIST_FETCHED, paged));
    }

    // UPDATE
    @Operation(summary = "Update submission")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SewEarnSubmitResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SewEarnSubmitRequest request) {

        SewEarnSubmitResponse dto = service.updateSubmission(id, request);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.SUBMIT_UPDATED, dto));
    }

    // DELETE
    @Operation(summary = "Delete submission")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get completed but not yet submitted items (grouped by material)")
    @GetMapping("/awaiting")
    public ResponseEntity<ApiResponse<List<AwaitingSubmissionMaterialResponse>>> awaiting() {
        List<AwaitingSubmissionMaterialResponse> list = service.getAwaitingForSubmission();
        return ResponseEntity.ok(ApiResponse.success("AWAITING_FOR_SUBMISSION_FETCHED", list));
    }
}
