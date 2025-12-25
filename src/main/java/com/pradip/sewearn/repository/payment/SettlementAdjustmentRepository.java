package com.pradip.sewearn.repository.payment;

import com.pradip.sewearn.model.payment.SettlementAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementAdjustmentRepository
        extends JpaRepository<SettlementAdjustment, Long> {

    /**
     * Fetch all adjustments for a settlement
     */
    List<SettlementAdjustment> findBySettlementId(Long settlementId);

    /**
     * Delete all adjustments of a settlement
     * (used when settlement is deleted)
     */
    void deleteBySettlementId(Long settlementId);
}
