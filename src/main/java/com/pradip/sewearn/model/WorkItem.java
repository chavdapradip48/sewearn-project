package com.pradip.sewearn.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "work_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private WorkSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private SewingMaterial material;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price")
    private Double totalPrice;
}
