package com.pradip.sewearn.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SettlementUpdateRequest {

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private Integer submittedTimesInDay;
    private Integer totalQuantity;

    private Long receivableAmount;
}
