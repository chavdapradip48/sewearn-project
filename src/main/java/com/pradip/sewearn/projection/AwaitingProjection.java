package com.pradip.sewearn.projection;

import java.time.LocalDate;

public interface AwaitingProjection {
    Long getReceivedItemId();
    Long getMaterialId();
    String getMaterialName();

    Integer getReceivedQuantity();
    Integer getCompletedQuantity();
    Integer getSubmittedQuantity();

    LocalDate getReceivedDate();
}

