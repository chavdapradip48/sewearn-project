package com.pradip.sewearn.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyProgressResponse {
    private Map<String, Long> dailyEarnings;
    private Long totalCurrentWeek;
    private Long totalPreviousWeek;
    private Long percentageComparedToLastWeek;
}