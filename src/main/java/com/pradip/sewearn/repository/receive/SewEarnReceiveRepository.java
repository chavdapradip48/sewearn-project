package com.pradip.sewearn.repository.receive;


import com.pradip.sewearn.model.receive.SewEarnReceive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("""
        SELECT r
        FROM SewEarnReceive r
        LEFT JOIN FETCH r.receivedItems ri
        LEFT JOIN FETCH ri.rawMaterialType
        WHERE r.markAsCompleted = :completed
        ORDER BY r.receivedDate DESC
    """)
    Page<SewEarnReceive> findAllByCompleted(boolean completed, Pageable pageable);

}
