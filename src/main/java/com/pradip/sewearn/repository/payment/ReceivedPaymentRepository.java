package com.pradip.sewearn.repository.payment;

import com.pradip.sewearn.model.payment.ReceivedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReceivedPaymentRepository
        extends JpaRepository<ReceivedPayment, Long> {

    /**
     * List all received payments for a settlement
     */
    @Query("""
        SELECT rp
        FROM ReceivedPayment rp
        JOIN SettlementPaymentAllocation a
             ON a.receivedPayment.id = rp.id
        WHERE a.settlement.id = :settlementId
        ORDER BY rp.receivedDate DESC
    """)
    List<ReceivedPayment> findBySettlementId(
            @Param("settlementId") Long settlementId
    );

    /**
     * Used to validate before deleting payment
     */
    boolean existsById(Long id);
}

