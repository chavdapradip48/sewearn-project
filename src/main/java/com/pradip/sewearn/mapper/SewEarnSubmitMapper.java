package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.submit.*;
import com.pradip.sewearn.model.submit.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SewEarnSubmitMapper {

    // =======================
    // Entity -> DTO
    // =======================
    public SewEarnSubmitResponse toDto(SewEarnSubmit submit) {

        return SewEarnSubmitResponse.builder()
                .id(submit.getId())
                .submissionDate(submit.getSubmissionDate())
                .totalQuantity(submit.getTotalQuantity())
                .totalEarning(submit.getTotalEarning())
                .submittedItems(
                        submit.getSubmittedItems()
                                .stream()
                                .map(this::toItemDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private SubmitItemResponse toItemDto(SubmitItem item) {
        return SubmitItemResponse.builder()
                .id(item.getId())
                .materialName(item.getRawMaterialType().getName())
                .quantity(item.getQuantity())
                .totalEarning(item.getTotalEarning())
                .build();
    }

    // =======================
    // DTO -> Entity
    // =======================
    public SewEarnSubmit toEntity(SewEarnSubmitRequest dto) {
        return SewEarnSubmit.builder()
                .submissionDate(dto.getSubmissionDate())
                .totalQuantity(0)
                .totalEarning(0L)
                .build();
    }

    public SubmitItem toItemEntity(SubmitItemRequest dto) {
        return SubmitItem.builder()
                .quantity(dto.getQuantity())
                .build();
    }

    public SewEarnSubmitSummaryResponse toSummary(SewEarnSubmit entity) {
        return SewEarnSubmitSummaryResponse.builder()
                .id(entity.getId())
                .submissionDate(entity.getSubmissionDate())
                .totalQuantity(entity.getTotalQuantity())
                .totalEarning(entity.getTotalEarning())
                .build();
    }
}
