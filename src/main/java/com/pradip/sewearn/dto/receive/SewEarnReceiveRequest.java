package com.pradip.sewearn.dto.receive;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnReceiveRequest {

    @NotNull(message = "receivedDate is required")
    private LocalDate receivedDate;

    @Size(min = 1, message = "At least one received item is required")
    private List<ReceivedItemRequest> receivedItems;
}
