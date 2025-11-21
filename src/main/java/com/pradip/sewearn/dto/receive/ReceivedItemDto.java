package com.pradip.sewearn.dto.receive;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceivedItemDto {

    private Long id;

    private Long materialTypeId;

    private String materialName;

    private Integer quantity;

    private Integer completedQuantity;

    private Integer pendingQuantity;
}
