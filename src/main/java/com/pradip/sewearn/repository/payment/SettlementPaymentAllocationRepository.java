package com.pradip.sewearn.repository.payment;

import com.pradip.sewearn.model.payment.SettlementPaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettlementPaymentAllocationRepository
        extends JpaRepository<SettlementPaymentAllocation, Long> {

    /**
     * All allocations for a settlement
     */
    List<SettlementPaymentAllocation> findBySettlementId(Long settlementId);

    /**
     * All allocations for a received payment
     * Used when payment is edited or deleted
     */
    List<SettlementPaymentAllocation> findByReceivedPaymentId(Long receivedPaymentId);

    /**
     * Sum allocated amount for a settlement
     * Used to update totalReceivedAmount
     */
    @Query("""
        SELECT COALESCE(SUM(a.allocatedAmount), 0)
        FROM SettlementPaymentAllocation a
        WHERE a.settlement.id = :settlementId
    """)
    Long sumAllocatedAmountBySettlementId(
            @Param("settlementId") Long settlementId
    );

    /**
     * Delete allocations of a received payment
     * Used when payment is deleted or reallocated
     */
    void deleteByReceivedPaymentId(Long receivedPaymentId);
}
