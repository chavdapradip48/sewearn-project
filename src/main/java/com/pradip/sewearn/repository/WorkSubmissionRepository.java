package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.WorkSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkSubmissionRepository extends JpaRepository<WorkSubmission, Long> {
    List<WorkSubmission> findBySubmissionDate(LocalDate submissionDate);
    List<WorkSubmission> findAllByOrderBySubmissionDateDesc();
    List<WorkSubmission> findAllByOrderBySubmissionDateAsc();
}