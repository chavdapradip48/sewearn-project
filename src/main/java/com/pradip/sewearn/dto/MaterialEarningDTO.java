package com.pradip.sewearn.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialEarningDTO {
    private String materialName;
    private Long earning;
}