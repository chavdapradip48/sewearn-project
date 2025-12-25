package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.MarkAsCompletedRequest;
import com.pradip.sewearn.dto.receive.*;

import com.pradip.sewearn.model.receive.SewEarnReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SewEarnReceiveService {

    SewEarnReceiveResponse createReceive(SewEarnReceiveRequest request);

    SewEarnReceiveResponse getReceiveById(Long id);

    Page<SewEarnReceive> getAllReceives(Pageable pageable);

    Page<SewEarnReceiveResponse> getReceivesByDate(LocalDate date, Pageable pageable);

    Page<SewEarnReceiveResponse> getReceivesByDateRange(LocalDate start, LocalDate end, Pageable pageable);

    SewEarnReceiveResponse updateReceive(Long id, SewEarnReceiveUpdateRequest request);

    void deleteReceive(Long id);

    List<SewEarnReceiveSummaryResponse> getAllReceivesSummary();

    // Summary (Paged)
    Page<SewEarnReceiveSummaryResponse> getAllReceivesSummaryPaged(Pageable pageable);

    Page<SewEarnReceiveSummaryResponse> getReceivesByDateSummary(LocalDate date, Pageable pageable);

    SewEarnReceiveResponse markAsCompleted(Long id, MarkAsCompletedRequest req);

    Page<SewEarnReceiveDto> getProgress(boolean completed, int page, int size);
}
