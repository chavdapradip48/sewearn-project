package com.pradip.sewearn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private Integer todayCompleted;
    private Long todayEarning;
    private Long monthlyEarning;
    private Integer pending;
}