package com.pradip.sewearn.model.payment;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sewearn_settlement_payment_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementPaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReceivedPayment receivedPayment;

    private Long allocatedAmount;
}
