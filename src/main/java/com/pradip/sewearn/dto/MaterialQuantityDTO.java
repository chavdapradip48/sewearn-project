package com.pradip.sewearn.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialQuantityDTO {
    private String materialName;
    private Integer totalQuantity;
}