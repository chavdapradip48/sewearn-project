package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.receive.ItemTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ItemTrackRepository extends JpaRepository<ItemTrack, Long> {

    List<ItemTrack> findByReceivedItemId(Long receivedItemId);

    List<ItemTrack> findByCompletedDate(LocalDate completedDate);
}
