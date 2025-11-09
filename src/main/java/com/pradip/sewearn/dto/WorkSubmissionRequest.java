package com.pradip.sewearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WorkSubmissionRequest {

    @NotNull(message = "Submission date is required")
    @Schema(description = "Date of work submission (e.g. 2025-11-10)")
    private LocalDate submissionDate;

    @NotEmpty(message = "Work items cannot be empty")
    @Schema(description = "List of work items included in this submission")
    private List<WorkItemRequest> items;
}
