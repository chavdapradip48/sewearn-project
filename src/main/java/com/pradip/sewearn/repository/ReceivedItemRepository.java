package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.receive.ReceivedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceivedItemRepository extends JpaRepository<ReceivedItem, Long> {

    List<ReceivedItem> findByReceiveId(Long receiveId);
}
