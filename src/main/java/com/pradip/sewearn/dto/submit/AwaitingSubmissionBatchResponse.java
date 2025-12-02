package com.pradip.sewearn.dto.submit;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwaitingSubmissionBatchResponse {

    private Long receivedItemId;
    private LocalDate receivedDate;

    private Integer receivedQuantity;
    private Integer completedQuantity;
    private Integer alreadySubmitted;
    private Integer pendingQuantity;
}
