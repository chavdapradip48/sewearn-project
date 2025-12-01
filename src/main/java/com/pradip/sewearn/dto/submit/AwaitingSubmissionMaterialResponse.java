package com.pradip.sewearn.dto.submit;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwaitingSubmissionMaterialResponse {

    private Long materialId;
    private String materialName;
    private Integer totalPendingQuantity;

    private List<AwaitingSubmissionBatchResponse> batches;
}
