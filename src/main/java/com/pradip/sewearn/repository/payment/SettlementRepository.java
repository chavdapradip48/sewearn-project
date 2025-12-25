package com.pradip.sewearn.repository.payment;

import com.pradip.sewearn.enums.SettlementStatus;
import com.pradip.sewearn.model.payment.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    /**
     * Get last settlement by period end date
     * Used for:
     * - default start date
     * - previous pending calculation
     */
    Optional<Settlement> findTopByOrderByPeriodEndDateDesc();

    /**
     * Check overlapping settlements
     * Prevent duplicate / overlapping periods
     */
    @Query("""
        SELECT COUNT(s) > 0
        FROM Settlement s
        WHERE
            (:startDate <= s.periodEndDate)
            AND (:endDate >= s.periodStartDate)
            AND (:excludeId IS NULL OR s.id <> :excludeId)
    """)
    boolean existsOverlappingSettlement(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );

    /**
     * Summary listing (lightweight)
     */
    @Query("""
        SELECT s
        FROM Settlement s
        ORDER BY s.periodStartDate DESC
    """)
    Page<Settlement> findAllSummary(Pageable pageable);

    /**
     * Settlement list with filters
     */
    @Query("""
        SELECT s
        FROM Settlement s
        WHERE
            (:fromDate IS NULL OR s.periodStartDate >= :fromDate)
            AND (:toDate IS NULL OR s.periodEndDate <= :toDate)
            AND (:status IS NULL OR s.status = :status)
        ORDER BY s.periodStartDate DESC
    """)
    Page<Settlement> findAllFiltered(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") SettlementStatus status,
            Pageable pageable
    );
}
