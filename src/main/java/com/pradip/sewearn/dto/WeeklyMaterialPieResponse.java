package com.pradip.sewearn.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeeklyMaterialPieResponse {
    private int week;
    private List<MaterialEarningDTO> materials;
}