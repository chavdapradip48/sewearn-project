package com.pradip.sewearn.dto.receive;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnReceiveResponse {

    private Long id;

    private LocalDate receivedDate;

    private Integer totalReceivedQuantity;
    private Double totalEarning;
    private Boolean markAsCompleted = false;
    private List<ReceivedItemResponse> receivedItems;
}
