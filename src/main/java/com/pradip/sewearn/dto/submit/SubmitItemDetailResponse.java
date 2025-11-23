package com.pradip.sewearn.dto.submit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemDetailResponse {
    private Long receivedItemId;
    private LocalDate receivedDate;
    private Integer quantity;
}
