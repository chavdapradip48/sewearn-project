package com.pradip.sewearn.model.submit;

import com.pradip.sewearn.model.BaseEntity;
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
public class SewEarnSubmit extends BaseEntity {

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
    public void removeSubmittedItem(SubmitItem item) {
        submittedItems.remove(item);
        item.setSubmit(this);
    }

}
