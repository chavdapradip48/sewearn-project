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
public class RawMaterialType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private Long price;
}
