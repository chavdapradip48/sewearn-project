package com.pradip.sewearn.model.receive;

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
public class ItemTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate completedDate;

    private Integer completedQuantity;   // daily completed number

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_item_id")
    private ReceivedItem receivedItem;
}