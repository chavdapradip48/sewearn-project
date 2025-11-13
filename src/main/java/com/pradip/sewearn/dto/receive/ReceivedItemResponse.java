package com.pradip.sewearn.dto.receive;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedItemResponse {

    private Long id;

    private String materialName;
    private Integer quantity;
    private Integer totalCompletedQuantity;

    private List<ItemTrackResponse> itemTracks;
}
