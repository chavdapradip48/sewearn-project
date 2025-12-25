package com.pradip.sewearn.repository.submit;

import com.pradip.sewearn.model.submit.SubmitItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmitItemRepository extends JpaRepository<SubmitItem, Long> {

    List<SubmitItem> findBySubmitId(Long submitId);
}