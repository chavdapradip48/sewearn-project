package com.pradip.sewearn.dto.submit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SWSubmitItemUpdateRequest {
    private Long id;

    @NotNull
    private Long receiveItemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
