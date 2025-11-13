package com.pradip.sewearn.dto.receive;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedItemCreateRequest {

    @NotNull(message = "receiveId is required")
    private Long receiveId;

    private Long materialId;
    private String materialName;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
}
