package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.WorkSubmissionRequest;
import com.pradip.sewearn.dto.WorkSubmissionSummaryResponse;
import com.pradip.sewearn.model.WorkSubmission;

import java.time.LocalDate;
import java.util.List;

public interface WorkSubmissionService {

    WorkSubmission createSubmission(WorkSubmissionRequest request);

    WorkSubmission getSubmissionById(Long id);

    List<WorkSubmission> getAllSubmissions();

    WorkSubmission updateSubmission(Long id, WorkSubmissionRequest request);

    void deleteSubmission(Long id);

    List<WorkSubmission> getSubmissionsByDate(LocalDate date);

    List<WorkSubmissionSummaryResponse> getAllSubmissionsSummary();
}