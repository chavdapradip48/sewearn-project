package com.pradip.sewearn.dto.payment;

import com.pradip.sewearn.enums.SettlementStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class SettlementDetailResponse {

    private Long id;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate calculatedDate;

    private Integer submittedTimesInDay;
    private Integer totalQuantity;

    private Long receivableAmount;
    private Long totalReceivedAmount;

    private Long netPayableAmount;
    private Long pendingAmount;

    private SettlementStatus status;

    private List<SettlementAdjustmentResponse> adjustments;
    private List<ReceivedPaymentResponse> receivedPayments;
    private List<SettlementAllocationResponse> allocations;
}
