package com.pradip.sewearn.mapper;

import com.pradip.sewearn.dto.WorkSubmissionResponse;
import com.pradip.sewearn.model.WorkSubmission;

import java.util.stream.Collectors;

public class WorkSubmissionMapper {

    public static WorkSubmissionResponse toDto(WorkSubmission submission) {
        if (submission == null) return null;

        return WorkSubmissionResponse.builder()
                .id(submission.getId())
                .submissionDate(submission.getSubmissionDate())
                .totalEarning(submission.getTotalEarning())
                .items(submission.getItems()
                        .stream()
                        .map(WorkItemMapper::toDto)
                        .collect(Collectors.toList()))
                .createdAt(submission.getCreatedAt())
                .build();
    }
}