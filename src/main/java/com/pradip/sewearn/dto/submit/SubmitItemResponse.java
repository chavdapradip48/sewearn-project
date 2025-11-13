package com.pradip.sewearn.dto.submit;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemResponse {

    private Long id;
    private String materialName;
    private Integer quantity;
    private Double totalEarning;
    private Long submitId;
}
