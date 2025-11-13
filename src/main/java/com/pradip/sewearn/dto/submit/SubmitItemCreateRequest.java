package com.pradip.sewearn.dto.submit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemCreateRequest {

    @NotNull(message = "submitId is required")
    private Long submitId;

    private Long materialId;
    private String materialName;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
}
