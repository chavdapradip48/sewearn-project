package com.pradip.sewearn.model.submit;

import com.pradip.sewearn.model.RawMaterialType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submit_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private Long totalEarning;  // quantity * material_price

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_type_id")
    private RawMaterialType rawMaterialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submit_id")
    private SewEarnSubmit submit;
}
