package com.pradip.sewearn.repository.submit;

import com.pradip.sewearn.model.submit.SewEarnSubmit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SewEarnSubmitRepository extends JpaRepository<SewEarnSubmit, Long> {

    // Pagination + Sorting
    Page<SewEarnSubmit> findAll(Pageable pageable);

    // Sorting explicitly by date
    Page<SewEarnSubmit> findAllByOrderBySubmissionDateDesc(Pageable pageable);
    Page<SewEarnSubmit> findAllByOrderBySubmissionDateAsc(Pageable pageable);

    // Filter by date
    Page<SewEarnSubmit> findBySubmissionDate(LocalDate date, Pageable pageable);

    // Filter by date range
    Page<SewEarnSubmit> findBySubmissionDateBetween(LocalDate start, LocalDate end, Pageable pageable);
    List<SewEarnSubmit> findBySubmissionDateBetween(LocalDate start, LocalDate end);
}
