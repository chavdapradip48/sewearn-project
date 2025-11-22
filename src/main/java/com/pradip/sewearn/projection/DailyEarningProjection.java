package com.pradip.sewearn.projection;

import org.hibernate.mapping.TableOwner;

import java.time.LocalDate;

public interface DailyEarningProjection {
    LocalDate getCompletedDate();
    Long getTotalEarning();
}