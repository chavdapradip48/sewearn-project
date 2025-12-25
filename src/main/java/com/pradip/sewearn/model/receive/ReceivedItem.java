package com.pradip.sewearn.model.receive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pradip.sewearn.model.BaseEntity;
import com.pradip.sewearn.model.RawMaterialType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sewearn_receive_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedItem extends BaseEntity {

    private Integer quantity;               // received quantity
    private Integer totalCompletedQuantity; // accumulated from item tracks

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_type_id")
    private RawMaterialType rawMaterialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_id")
    @JsonIgnore
    private SewEarnReceive receive;

    @OneToMany(mappedBy = "receivedItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemTrack> itemTracks = new ArrayList<>();

    public void addItemTrack(ItemTrack track) {
        itemTracks.add(track);
        track.setReceivedItem(this);
    }
}
