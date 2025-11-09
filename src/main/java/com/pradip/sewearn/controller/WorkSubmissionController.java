package com.pradip.sewearn.controller;

import com.pradip.sewearn.dto.WorkSubmissionRequest;
import com.pradip.sewearn.dto.WorkSubmissionResponse;
import com.pradip.sewearn.dto.WorkSubmissionSummaryResponse;
import com.pradip.sewearn.mapper.WorkSubmissionMapper;
import com.pradip.sewearn.model.WorkSubmission;
import com.pradip.sewearn.service.WorkSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-submissions")
@RequiredArgsConstructor
@Tag(name = "Work Submissions", description = "APIs for managing daily work submissions and earnings summary")
public class WorkSubmissionController {

    private final WorkSubmissionService submissionService;

    @Operation(summary = "Create new work submission",
            description = "Creates a new work submission with one or more work items. " +
                    "Each item must reference a valid material by ID or name.")
    @PostMapping
    public ResponseEntity<WorkSubmissionResponse> createSubmission(
            @Valid @RequestBody WorkSubmissionRequest request) {

        WorkSubmission submission = submissionService.createSubmission(request);
        WorkSubmissionResponse response = WorkSubmissionMapper.toDto(submission);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update work submission",
            description = "Update an existing work submission and its associated work items.")
    @PutMapping("/{id}")
    public ResponseEntity<WorkSubmissionResponse> updateSubmission(
            @PathVariable Long id,
            @Valid @RequestBody WorkSubmissionRequest request) {

        WorkSubmission updated = submissionService.updateSubmission(id, request);
        WorkSubmissionResponse response = WorkSubmissionMapper.toDto(updated);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all submissions summary",
            description = "Retrieve all work submissions with summary details (date, total items, and total earnings).")
    @GetMapping("/summery")
    public ResponseEntity<List<WorkSubmissionSummaryResponse>> getAllSubmissions() {
        List<WorkSubmissionSummaryResponse> summaries = submissionService.getAllSubmissionsSummary();
        return ResponseEntity.ok(summaries);
    }

    @Operation(summary = "Get all work submissions with items",
            description = "Retrieve all work submissions along with their associated work items.")
    @GetMapping("/listing")
    public ResponseEntity<List<WorkSubmissionResponse>> getAllSubmissionsWithItems() {
        List<WorkSubmissionResponse> submissions = submissionService.getAllSubmissions().stream()
                .map(WorkSubmissionMapper::toDto)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @Operation(summary = "Get submissions by date",
            description = "Retrieve all submissions made on a specific date.")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<WorkSubmissionResponse>> getSubmissionsByDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);

        List<WorkSubmissionResponse> submissions = submissionService.getSubmissionsByDate(parsedDate)
                .stream()
                .map(WorkSubmissionMapper::toDto)
                .toList();

        return ResponseEntity.ok(submissions);
    }

    @Operation(summary = "Get submission by ID",
            description = "Retrieve a specific work submission and its associated items by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<WorkSubmissionResponse> getSubmissionById(@PathVariable Long id) {
        WorkSubmission submission = submissionService.getSubmissionById(id);
        WorkSubmissionResponse response = WorkSubmissionMapper.toDto(submission);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete submission",
            description = "Delete a work submission and its related items permanently.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
