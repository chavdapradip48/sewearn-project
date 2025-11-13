package com.pradip.sewearn.repository;


import com.pradip.sewearn.model.receive.SewEarnReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SewEarnReceiveRepository extends JpaRepository<SewEarnReceive, Long> {

    // Pagination + Sorting
    Page<SewEarnReceive> findAll(Pageable pageable);

    // Sorting explicitly by receivedDate
    Page<SewEarnReceive> findAllByOrderByReceivedDateDesc(Pageable pageable);
    Page<SewEarnReceive> findAllByOrderByReceivedDateAsc(Pageable pageable);

    // Filter by specific date
    Page<SewEarnReceive> findByReceivedDate(LocalDate date, Pageable pageable);

    // Filter by date range
    Page<SewEarnReceive> findByReceivedDateBetween(LocalDate start, LocalDate end, Pageable pageable);
}
