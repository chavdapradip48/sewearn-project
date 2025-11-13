package com.pradip.sewearn.model.receive;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sewearn_receive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate receivedDate;

    private Integer totalReceivedQuantity;   // Sum of all received items
    private Double totalEarning;             // Based on material price if needed

    @OneToMany(mappedBy = "receive", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReceivedItem> receivedItems = new ArrayList<>();

    public void addReceivedItem(ReceivedItem item) {
        receivedItems.add(item);
        item.setReceive(this);
    }
}
