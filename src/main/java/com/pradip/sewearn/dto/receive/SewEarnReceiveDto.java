package com.pradip.sewearn.dto.receive;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SewEarnReceiveDto {

    private Long id;

    private LocalDate receivedDate;

    private Integer totalReceivedQuantity;

    private Long totalEarning;

    private Boolean markAsCompleted;

    private List<ReceivedItemDto> items;
}
