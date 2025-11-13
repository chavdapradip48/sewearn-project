package com.pradip.sewearn.dto.receive;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTrackRequest {

    @NotNull(message = "completedDate is required")
    private LocalDate completedDate;

    @NotNull(message = "completedQuantity is required")
    @Min(value = 1, message = "completedQuantity must be at least 1")
    private Integer completedQuantity;
}
