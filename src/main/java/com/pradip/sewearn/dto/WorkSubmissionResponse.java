package com.pradip.sewearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class WorkSubmissionResponse {

    private Long id;

    @Schema(description = "Date when the submission was made")
    private LocalDate submissionDate;

    @Schema(description = "Total earning calculated for this submission")
    private Double totalEarning;

    @Schema(description = "List of work items in this submission")
    private List<WorkItemResponse> items;

    @Schema(description = "Timestamp when submission record was created")
    private LocalDateTime createdAt;
}