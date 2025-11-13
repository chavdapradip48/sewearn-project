package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.RawMaterialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RawMaterialTypeRepository extends JpaRepository<RawMaterialType, Long> {

    Optional<RawMaterialType> findByNameIgnoreCase(String name);
}