package com.pradip.sewearn.model.submit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sewearn_submit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SewEarnSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate submissionDate;

    private Long totalEarning;

    private Integer totalQuantity;

    @OneToMany(mappedBy = "submit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmitItem> submittedItems = new ArrayList<>();

    public void addSubmittedItem(SubmitItem item) {
        submittedItems.add(item);
        item.setSubmit(this);
    }
}
