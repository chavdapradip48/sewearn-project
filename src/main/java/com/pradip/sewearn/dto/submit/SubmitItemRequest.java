package com.pradip.sewearn.dto.submit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemRequest {

    private Long materialId;
    private String materialName;

    @NotNull @Min(1)
    private Integer quantity;
}
