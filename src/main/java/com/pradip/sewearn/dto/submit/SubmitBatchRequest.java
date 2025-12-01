package com.pradip.sewearn.dto.submit;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitBatchRequest {

    private Long receivedItemId;   // which batch
    private Integer quantity;      // how much from this batch
}
