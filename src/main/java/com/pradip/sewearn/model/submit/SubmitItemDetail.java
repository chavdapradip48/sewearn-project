package com.pradip.sewearn.model.submit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pradip.sewearn.model.BaseEntity;
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
public class SubmitItemDetail extends BaseEntity {

    @Column(nullable = false)
    private Integer quantity;

    private LocalDate receivedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_item_id", nullable = false)
    @JsonIgnore
    private SubmitItem submitItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_item_id", nullable = false)
    private ReceivedItem receivedItem;
}
