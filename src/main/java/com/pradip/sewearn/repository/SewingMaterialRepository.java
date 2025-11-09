package com.pradip.sewearn.repository;

import com.pradip.sewearn.model.SewingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SewingMaterialRepository extends JpaRepository<SewingMaterial, Long> {

    /**
     * Check if a material with given name already exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if a material name exists excluding a specific ID (used for update validation)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    /**
     * Find material by name (case-insensitive)
     */
    Optional<SewingMaterial> findByNameIgnoreCase(String name);
}