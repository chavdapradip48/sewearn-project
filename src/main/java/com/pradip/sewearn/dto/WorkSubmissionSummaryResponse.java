package com.pradip.sewearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Summary view of work submissions with total items and earnings")
public class WorkSubmissionSummaryResponse {

    @Schema(description = "Date of the submission")
    private LocalDate submissionDate;

    @Schema(description = "Total number of items submitted on this date")
    private int totalItems;

    @Schema(description = "Total earnings for the given submission date")
    private double totalEarning;
}