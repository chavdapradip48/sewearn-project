package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.DashboardSummaryResponse;
import com.pradip.sewearn.dto.WeeklyMaterialPieResponse;
import com.pradip.sewearn.dto.WeeklyProgressResponse;

public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary();

    WeeklyProgressResponse getWeeklyProgress(int weekOffset);

    WeeklyMaterialPieResponse getWeeklyMaterialWisePie(int weekOffset);
}
