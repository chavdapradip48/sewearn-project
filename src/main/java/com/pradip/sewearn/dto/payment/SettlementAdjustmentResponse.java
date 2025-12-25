package com.pradip.sewearn.dto.payment;

import com.pradip.sewearn.enums.AdjustmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SettlementAdjustmentResponse {

    private Long id;
    private String description;
    private Long amount;
    private AdjustmentType type;
}
