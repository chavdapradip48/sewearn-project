package com.pradip.sewearn.dto.receive;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnReceiveUpdateRequest {

    @NotNull
    private LocalDate receivedDate;

    @NotEmpty
    private List<SWReceivedItemUpdateRequest> receivedItems;
}

