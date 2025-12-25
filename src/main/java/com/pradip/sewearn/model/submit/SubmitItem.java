package com.pradip.sewearn.model.submit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pradip.sewearn.model.BaseEntity;
import com.pradip.sewearn.model.RawMaterialType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submit_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItem extends BaseEntity {

    private Integer quantity;

    private Long totalEarning;  // quantity * material_price

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_type_id")
    private RawMaterialType rawMaterialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_id")
    @JsonIgnore
    private SewEarnSubmit submit;

    @OneToMany(mappedBy = "submitItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmitItemDetail> details = new ArrayList<>();

    public void addDetail(SubmitItemDetail detail) {details.add(detail); detail.setSubmitItem(this); }
    public void removeDetail(SubmitItemDetail d) { details.remove(d); d.setSubmitItem(null); }
}
