package com.pradip.sewearn.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SettlementCalculateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}