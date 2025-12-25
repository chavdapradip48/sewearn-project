package com.pradip.sewearn.dto.payment;

import com.pradip.sewearn.enums.AdjustmentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementAdjustmentRequest {

    private String description;
    private Long amount;
    private AdjustmentType type; // EXPENSE or EXTRA
}
