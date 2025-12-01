package com.pradip.sewearn.dto.submit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemRequest {

    @NotNull
    private Long materialId;

    @NotNull @Min(1)
    private Integer totalSubmitQuantity;

    @NotNull(message = "batches are required")
    private List<SubmitBatchRequest> batches;  // NEW
}
