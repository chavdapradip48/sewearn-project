package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.receive.ReceivedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReceivedItemRepository extends JpaRepository<ReceivedItem, Long> {

    List<ReceivedItem> findByReceiveId(Long receiveId);

    @Query("""
        SELECT COALESCE(SUM(ri.quantity - ri.totalCompletedQuantity), 0)
        FROM ReceivedItem ri
    """)
    Integer getTotalPending();

    @Query("""
    SELECT COUNT(ri)
    FROM ReceivedItem ri
    WHERE ri.receive.id = :receiveId
    AND ri.totalCompletedQuantity < ri.quantity
    """)
    long countPendingItems(Long receiveId);
}
