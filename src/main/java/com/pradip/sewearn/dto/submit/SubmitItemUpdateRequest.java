package com.pradip.sewearn.dto.submit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemUpdateRequest {

    private Long materialId;
    private String materialName;

    @NotNull(message = "quantity is required")
    @Min(1)
    private Integer quantity;
}
