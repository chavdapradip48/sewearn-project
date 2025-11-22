package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.receive.ItemTrack;
import com.pradip.sewearn.projection.DailyEarningProjection;
import com.pradip.sewearn.projection.MaterialQuantityProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ItemTrackRepository extends CrudRepository<ItemTrack, Long> {

    List<ItemTrack> findByReceivedItemId(Long receivedItemId);

    List<ItemTrack> findByCompletedDate(LocalDate completedDate);
    @Query("""
        SELECT COALESCE(SUM(t.completedQuantity * rt.price), 0)
        FROM ItemTrack t
        JOIN t.receivedItem ri
        JOIN ri.rawMaterialType rt
        WHERE t.completedDate BETWEEN :start AND :end
    """)
    Long getEarningsBetween(LocalDate start, LocalDate end);

    @Query("""
        SELECT t.completedDate AS completedDate,
               SUM(t.completedQuantity * rt.price) AS totalEarning
        FROM ItemTrack t
        JOIN t.receivedItem ri
        JOIN ri.rawMaterialType rt
        WHERE t.completedDate BETWEEN :start AND :end
        GROUP BY t.completedDate
        ORDER BY t.completedDate
    """)
    List<DailyEarningProjection> getDailyEarningsBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Today earnings
    @Query("""
        SELECT COALESCE(SUM(t.completedQuantity * rt.price), 0)
        FROM ItemTrack t
        JOIN t.receivedItem ri
        JOIN ri.rawMaterialType rt
        WHERE t.completedDate = :today
    """)
    Long getTodaysEarnings(LocalDate today);

    // Today completed count
    @Query("""
        SELECT COALESCE(SUM(t.completedQuantity), 0)
        FROM ItemTrack t
        WHERE t.completedDate = :today
    """)
    Integer getTodaysCompleted(LocalDate today);

    @Query("""
        SELECT ri.rawMaterialType.name AS materialName,
               SUM(t.completedQuantity) AS totalQuantity
        FROM ItemTrack t
        JOIN t.receivedItem ri
        WHERE t.completedDate BETWEEN :start AND :end
        GROUP BY ri.rawMaterialType.name
    """)
    List<MaterialQuantityProjection> getMaterialWiseCompletedBetween(
            LocalDate start, LocalDate end
    );
}
