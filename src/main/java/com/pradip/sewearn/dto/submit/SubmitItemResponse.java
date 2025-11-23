package com.pradip.sewearn.dto.submit;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemResponse {

    private Long id;
    private String materialName;
    private Integer quantity;
    private Long totalEarning;
    private Long submitId;
    private List<SubmitItemDetailResponse> details;
}
