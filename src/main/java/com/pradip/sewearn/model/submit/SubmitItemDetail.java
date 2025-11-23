package com.pradip.sewearn.model.submit;

import com.pradip.sewearn.model.receive.ReceivedItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "submit_item_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItemDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quantity taken from this received batch
    @Column(nullable = false)
    private Integer quantity;

    // For reporting, quick access
    private LocalDate receivedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_item_id", nullable = false)
    private SubmitItem submitItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_item_id", nullable = false)
    private ReceivedItem receivedItem;
}
