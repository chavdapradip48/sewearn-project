package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.submit.SubmitItemDetail;
import com.pradip.sewearn.projection.AwaitingProjection;
import com.pradip.sewearn.projection.SubmittedSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmitItemDetailRepository extends JpaRepository<SubmitItemDetail, Long> {

    @Query("""
        SELECT COALESCE(SUM(d.quantity), 0) 
        FROM SubmitItemDetail d 
        WHERE d.receivedItem.id = :receivedItemId
    """)
    int sumSubmittedForReceivedItem(@Param("receivedItemId") Long receivedItemId);

    @Query("""
        SELECT
            ri.id AS receivedItemId,
            rm.id AS materialId,
            rm.name AS materialName,
            ri.quantity AS receivedQuantity,
            ri.totalCompletedQuantity AS completedQuantity,
            COALESCE(SUM(d.quantity), 0) AS submittedQuantity,
            rcv.receivedDate AS receivedDate
        FROM ReceivedItem ri
        JOIN ri.rawMaterialType rm
        JOIN ri.receive rcv
        LEFT JOIN SubmitItemDetail d ON d.receivedItem = ri
        GROUP BY ri.id, rm.id, rm.name, ri.quantity, ri.totalCompletedQuantity, rcv.receivedDate
    """)
    List<AwaitingProjection> findAwaitingSummary();



    /**
     * Fetch submitted sums grouped by receivedItemId for the given batch ids.
     * Single-query solution to avoid N+1.
     */
    @Query("""
        SELECT d.receivedItem.id AS receivedItemId,
               COALESCE(SUM(d.quantity), 0) AS submittedQty
        FROM SubmitItemDetail d
        WHERE d.receivedItem.id IN :ids
        GROUP BY d.receivedItem.id
    """)
    List<SubmittedSummaryProjection> getSubmittedSummaryForBatchIds(@Param("ids") List<Long> ids);

    /**
     * Delete all submit-item-detail rows that belong to a given submission (via submit id).
     * Used during update to remove previous allocation details for the same submission.
     */
    @Modifying
    @Query("DELETE FROM SubmitItemDetail d WHERE d.submitItem.submit.id = :submitId")
    void deleteBySubmitId(@Param("submitId") Long submitId);

}
