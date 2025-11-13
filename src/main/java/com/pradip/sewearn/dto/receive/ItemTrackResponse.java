package com.pradip.sewearn.dto.receive;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTrackResponse {

    private Long id;
    private LocalDate completedDate;
    private Integer completedQuantity;
    private Long receivedItemId;
}
