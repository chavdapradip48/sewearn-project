package com.pradip.sewearn.service.impl;


import com.pradip.sewearn.dto.DashboardSummaryResponse;
import com.pradip.sewearn.dto.MaterialEarningDTO;
import com.pradip.sewearn.dto.WeeklyMaterialPieResponse;
import com.pradip.sewearn.dto.WeeklyProgressResponse;
import com.pradip.sewearn.projection.DailyEarningProjection;
import com.pradip.sewearn.projection.MaterialEarningProjection;
import com.pradip.sewearn.repository.ItemTrackRepository;
import com.pradip.sewearn.repository.ReceivedItemRepository;
import com.pradip.sewearn.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ItemTrackRepository trackRepo;
    private final ReceivedItemRepository receivedItemRepo;

    @Override
    public DashboardSummaryResponse getDashboardSummary() {

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        Double monthlyEarnings = trackRepo.getEarningsBetween(startOfMonth, endOfMonth);
        Double todaysEarnings = trackRepo.getTodaysEarnings(today);
        Integer todaysCompleted = trackRepo.getTodaysCompleted(today);
        Integer pending = receivedItemRepo.getTotalPending();

        return DashboardSummaryResponse.builder()
                .monthlyEarning(monthlyEarnings)
                .todayEarning(todaysEarnings)
                .todayCompleted(todaysCompleted)
                .pending(pending)
                .build();
    }

    @Override
    public WeeklyProgressResponse getWeeklyProgress(int weekOffset) {

        // 1️⃣ Determine current week range
        LocalDate today = LocalDate.now().plusWeeks(weekOffset);
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        // 2️⃣ Previous week range
        LocalDate prevStart = startOfWeek.minusWeeks(1);
        LocalDate prevEnd = endOfWeek.minusWeeks(1);

        // 3️⃣ Prepare dynamic map for Mon→Sun with zero values
        Map<String, Double> daily = new LinkedHashMap<>();
        LocalDate temp = startOfWeek;

        for (int i = 0; i < 7; i++) {
            String shortDay = formatDay(temp.getDayOfWeek());
            daily.put(shortDay, 0.0);
            temp = temp.plusDays(1);
        }

        // 4️⃣ Fetch completed earnings for current week
        List<DailyEarningProjection> rows =
                trackRepo.getDailyEarningsBetween(startOfWeek, endOfWeek);

        for (DailyEarningProjection row : rows) {
            String day = formatDay(row.getCompletedDate().getDayOfWeek());
            daily.put(day, row.getTotalEarning());
        }

        // 5️⃣ Weekly totals
        double totalCurrentWeek = daily.values().stream().mapToDouble(Double::doubleValue).sum();

        List<DailyEarningProjection> prevRows =
                trackRepo.getDailyEarningsBetween(prevStart, prevEnd);

        double totalPrevWeek = prevRows.stream()
                .mapToDouble(DailyEarningProjection::getTotalEarning)
                .sum();

        // 6️⃣ % Comparison
        double perc = 0.0;
        if (totalPrevWeek > 0) {
            perc = ((totalCurrentWeek - totalPrevWeek) / totalPrevWeek) * 100;
        }

        return WeeklyProgressResponse.builder()
                .dailyEarnings(daily)
                .totalCurrentWeek(totalCurrentWeek)
                .totalPreviousWeek(totalPrevWeek)
                .percentageComparedToLastWeek(perc)
                .build();
    }

    private String formatDay(DayOfWeek dow) {
        return dow.name().substring(0, 1)
                + dow.name().substring(1, 3).toLowerCase();
    }

    @Override
    public WeeklyMaterialPieResponse getWeeklyMaterialWisePie(int weekOffset) {

        // Determine the week's date range
        LocalDate today = LocalDate.now().plusWeeks(weekOffset);
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        // Fetch grouped data
        List<MaterialEarningProjection> rows =
                trackRepo.getMaterialWiseEarningsBetween(startOfWeek, endOfWeek);

        // Convert to DTO list
        List<MaterialEarningDTO> materials = rows.stream()
                .map(r -> MaterialEarningDTO.builder()
                        .materialName(r.getMaterialName())
                        .earning(r.getTotalEarning())
                        .build())
                .toList();

        return WeeklyMaterialPieResponse.builder()
                .week(weekOffset)
                .materials(materials)
                .build();
    }
}