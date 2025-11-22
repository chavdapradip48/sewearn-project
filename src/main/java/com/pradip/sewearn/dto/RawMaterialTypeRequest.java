package com.pradip.sewearn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialTypeRequest {

    @NotBlank(message = "Material name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Long price;
}
