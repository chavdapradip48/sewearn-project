package com.pradip.sewearn.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialTypeResponse {

    private Long id;
    private String name;
    private Long price;
}