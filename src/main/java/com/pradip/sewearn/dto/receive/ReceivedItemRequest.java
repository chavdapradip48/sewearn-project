package com.pradip.sewearn.dto.receive;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedItemRequest {

    // Either rawMaterialTypeId or rawMaterialTypeName should be provided
    private Long rawMaterialTypeId;
    private String rawMaterialTypeName;

    @NotNull @Min(1)
    private Integer quantity;
}
