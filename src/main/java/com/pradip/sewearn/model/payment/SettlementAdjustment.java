package com.pradip.sewearn.model.payment;

import com.pradip.sewearn.enums.AdjustmentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sewearn_settlement_adjustment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;

    private String description;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private AdjustmentType type;
}

