package com.pradip.sewearn.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyProgressResponse {
    private Map<String, Double> dailyEarnings;
    private Double totalCurrentWeek;
    private Double totalPreviousWeek;
    private Double percentageComparedToLastWeek;
}