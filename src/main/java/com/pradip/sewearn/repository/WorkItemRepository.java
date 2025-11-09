package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.WorkItem;
import com.pradip.sewearn.model.WorkSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
}