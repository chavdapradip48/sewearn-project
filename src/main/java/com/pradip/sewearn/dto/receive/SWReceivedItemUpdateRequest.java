package com.pradip.sewearn.dto.receive;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SWReceivedItemUpdateRequest {
    private Long id;
    private Long materialId;
    private String materialName;

    @NotNull(message = "quantity is required")
    @Min(1)
    private Integer quantity;
}
