package com.pradip.sewearn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewingMaterialResponse {

    private Long id;
    private String name;
    private Double price;
}