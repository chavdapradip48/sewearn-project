package com.pradip.sewearn.dto.receive;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SewEarnReceiveSummaryResponse {
    private Long id;
    private LocalDate receivedDate;
    private Integer totalReceivedQuantity;
    private Integer completedQuantity;
    private Long totalEarning;
}
