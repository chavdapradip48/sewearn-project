package com.pradip.sewearn.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SettlementAllocationResponse {

    private Long id;

    private Long settlementId;
    private Long receivedPaymentId;

    private Long allocatedAmount;
}
