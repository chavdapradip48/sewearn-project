package com.pradip.sewearn.model.receive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pradip.sewearn.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "item_track")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemTrack extends BaseEntity {


    private LocalDate completedDate;

    private Integer completedQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_item_id")
    @JsonIgnore
    private ReceivedItem receivedItem;
}