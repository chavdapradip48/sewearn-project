package com.pradip.sewearn.dto.submit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnSubmitRequest {

    @NotNull(message = "submissionDate is required")
    private LocalDate submissionDate;

    @Size(min = 1, message = "At least one submitted item is required")
    private List<SubmitItemRequest> items; // renamed from submittedItems
}
