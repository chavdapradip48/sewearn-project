package com.pradip.sewearn.projection;

import java.time.LocalDate;

public interface DailyEarningProjection {
    LocalDate getCompletedDate();
    Double getTotalEarning();
}