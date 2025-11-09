package com.pradip.sewearn.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class WorkItemResponse {

    private Long id;

    @Schema(description = "Sewing material name")
    private String materialName;

    @Schema(description = "Quantity of items submitted")
    private Integer quantity;

    @Schema(description = "Price per item")
    private BigDecimal unitPrice;

    @Schema(description = "Total calculated price")
    private BigDecimal totalPrice;

    @Schema(description = "Submission date")
    private LocalDate submissionDate;
}