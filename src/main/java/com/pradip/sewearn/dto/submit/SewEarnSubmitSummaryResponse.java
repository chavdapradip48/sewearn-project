package com.pradip.sewearn.dto.submit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SewEarnSubmitSummaryResponse {
    private Long id;
    private LocalDate submissionDate;
    private Integer totalQuantity;
    private Double totalEarning;
}
