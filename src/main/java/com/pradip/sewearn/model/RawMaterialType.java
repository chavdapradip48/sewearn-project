package com.pradip.sewearn.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raw_material_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Long price;
}
