package com.pradip.sewearn.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SettlementCalculateResponse {
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private Long previousPendingAmount;
    private Long receivableAmount;

    private Integer submittedTimesInDay;
    private Integer totalQuantity;
}
