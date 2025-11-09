package com.pradip.sewearn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewingMaterialRequest {

    @NotBlank(message = "Material name is required")
    private String name;

    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;
}