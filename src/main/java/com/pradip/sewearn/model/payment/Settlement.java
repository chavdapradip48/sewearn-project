package com.pradip.sewearn.model.payment;

import com.pradip.sewearn.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sewearn_settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate calculatedDate;

    private Integer submittedTimesInDay;
    private Integer totalQuantity;

    private Long receivableAmount;
    private Long totalReceivedAmount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;
}
