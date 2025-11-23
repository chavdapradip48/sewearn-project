package com.pradip.sewearn.service.impl;


import com.pradip.sewearn.dto.DashboardSummaryResponse;
import com.pradip.sewearn.dto.MaterialQuantityDTO;
import com.pradip.sewearn.dto.WeeklyMaterialPieResponse;
import com.pradip.sewearn.dto.WeeklyProgressResponse;
import com.pradip.sewearn.projection.DailyEarningProjection;
import com.pradip.sewearn.projection.MaterialQuantityProjection;
import com.pradip.sewearn.repository.ItemTrackRepository;
import com.pradip.sewearn.repository.ReceivedItemRepository;
import com.pradip.sewearn.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        Long monthlyEarnings = trackRepo.getEarningsBetween(startOfMonth, endOfMonth);
        Long todaysEarnings = trackRepo.getTodaysEarnings(today);
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
        Map<String, Long> daily = new LinkedHashMap<>();
        LocalDate temp = startOfWeek;

        for (int i = 0; i < 7; i++) {
            String shortDay = formatDay(temp.getDayOfWeek());
            daily.put(shortDay, 0L);
            temp = temp.plusDays(1);
        }

        // 4️⃣ Fetch completed earnings for current week
        List<DailyEarningProjection> rows =
                trackRepo.getDailyEarningsBetween(startOfWeek, endOfWeek);

        for (DailyEarningProjection row : rows) {
            String day = formatDay(row.getCompletedDate().getDayOfWeek());
            daily.put(day, row.getTotalEarning());
        }

        long totalCurrentWeek = daily.values().stream().mapToLong(Long::longValue).sum();

        List<DailyEarningProjection> prevRows = trackRepo.getDailyEarningsBetween(prevStart, prevEnd);

        long totalPrevWeek = prevRows.stream().mapToLong(DailyEarningProjection::getTotalEarning).sum();

        long perc = 0L;
        if (totalPrevWeek > 0) {
            double diff = totalCurrentWeek - totalPrevWeek;
            double percentage = (diff / totalPrevWeek) * 100;
            perc = Math.round(percentage);
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

        LocalDate today = LocalDate.now().plusWeeks(weekOffset);

        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<MaterialQuantityProjection> rows =
                trackRepo.getMaterialWiseCompletedBetween(startOfWeek, endOfWeek);

        long totalQtyOfWeek = rows.stream().mapToLong(MaterialQuantityProjection::getTotalQuantity).sum();

        List<MaterialQuantityDTO> materials = rows.stream()
                .map(r -> {
                    double upper = r.getTotalQuantity() * 100;
                    double percentage = BigDecimal.valueOf((upper) / totalQtyOfWeek).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    return MaterialQuantityDTO.builder()
                            .materialName(r.getMaterialName())
                            .totalQuantity(Math.toIntExact(r.getTotalQuantity()))
                            .percentage(percentage)
                            .build();
                })
                .toList();

        return WeeklyMaterialPieResponse.builder().week(weekOffset).totalQuantityOfWeek((int) totalQtyOfWeek).materials(materials).build();
    }

}