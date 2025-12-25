package com.pradip.sewearn.model.payment;

import com.pradip.sewearn.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sewearn_received_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate receivedDate;
    private Long receivedAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String reference;
}
