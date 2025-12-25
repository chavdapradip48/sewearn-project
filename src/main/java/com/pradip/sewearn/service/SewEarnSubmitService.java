package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.submit.AwaitingSubmissionMaterialResponse;
import com.pradip.sewearn.dto.submit.SewEarnSubmitRequest;
import com.pradip.sewearn.dto.submit.SewEarnSubmitResponse;
import com.pradip.sewearn.dto.submit.SewEarnSubmitSummaryResponse;
import com.pradip.sewearn.model.submit.SewEarnSubmit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SewEarnSubmitService {

    SewEarnSubmitResponse createSubmission(SewEarnSubmitRequest request);

    SewEarnSubmitResponse getSubmissionById(Long id);

    Page<SewEarnSubmit> getAllSubmissions(Pageable pageable);

    Page<SewEarnSubmitResponse> getSubmissionsByDate(LocalDate date, Pageable pageable);

    Page<SewEarnSubmitResponse> getSubmissionsByDateRange(LocalDate start, LocalDate end, Pageable pageable);

    SewEarnSubmitResponse updateSubmission(Long id, SewEarnSubmitRequest request);

    void deleteSubmission(Long id);

    // --- SUMMARY LISTS ----
    List<SewEarnSubmitSummaryResponse> getAllSubmissionsSummary();

    Page<SewEarnSubmitSummaryResponse> getAllSubmissionsSummaryPaged(Pageable pageable);

    Page<SewEarnSubmitSummaryResponse> getSubmissionsByDateSummary(LocalDate date, Pageable pageable);

    List<AwaitingSubmissionMaterialResponse> getAwaitingForSubmission();
}
