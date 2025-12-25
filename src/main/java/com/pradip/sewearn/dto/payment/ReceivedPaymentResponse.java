package com.pradip.sewearn.dto.payment;

import com.pradip.sewearn.enums.PaymentMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ReceivedPaymentResponse {

    private Long id;
    private LocalDate receivedDate;
    private Long receivedAmount;
    private PaymentMode paymentMode;
    private String reference;
}

