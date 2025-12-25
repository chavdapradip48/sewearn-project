package com.pradip.sewearn.dto.payment;

import com.pradip.sewearn.enums.SettlementStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SettlementResponse {

    private Long id;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate calculatedDate;

    private Integer submittedTimesInDay;
    private Integer totalQuantity;

    private Long receivableAmount;
    private Long totalReceivedAmount;

    private SettlementStatus status;
}
