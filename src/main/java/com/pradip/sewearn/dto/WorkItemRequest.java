package com.pradip.sewearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkItemRequest {

    @Schema(description = "Material ID (optional, use name if ID not provided)")
    private Long materialId;

    @Schema(description = "Material name (optional, use ID if available)")
    private String materialName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}