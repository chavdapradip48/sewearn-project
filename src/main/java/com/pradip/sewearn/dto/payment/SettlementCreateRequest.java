package com.pradip.sewearn.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SettlementCreateRequest {

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private Integer submittedTimesInDay;
    private Integer totalQuantity;
    private Long receivableAmount;

    private List<SettlementAdjustmentRequest> adjustments;
    private List<ReceivedPaymentRequest> receivedPayments;
}
