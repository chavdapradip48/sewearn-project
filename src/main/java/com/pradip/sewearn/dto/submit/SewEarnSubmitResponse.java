package com.pradip.sewearn.dto.submit;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnSubmitResponse {

    private Long id;
    private LocalDate submissionDate;

    private Integer totalQuantity;
    private Long totalEarning;

    private List<SubmitItemResponse> submittedItems;
}
