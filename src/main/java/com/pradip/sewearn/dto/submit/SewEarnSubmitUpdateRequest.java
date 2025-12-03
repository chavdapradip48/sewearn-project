package com.pradip.sewearn.dto.submit;

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
public class SewEarnSubmitUpdateRequest {

    @NotNull
    private LocalDate submittedDate;

    @NotEmpty
    private List<SWSubmitItemUpdateRequest> submittedItems;
}
