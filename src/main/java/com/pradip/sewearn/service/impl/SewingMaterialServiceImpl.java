package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.exception.custom.DuplicateResourceException;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.model.SewingMaterial;
import com.pradip.sewearn.repository.SewingMaterialRepository;
import com.pradip.sewearn.service.SewingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SewingMaterialServiceImpl implements SewingMaterialService {

    private final SewingMaterialRepository sewingMaterialRepository;

    /**
     * Create new sewing material
     * Ensures name uniqueness
     */
    @Override
    public SewingMaterial createMaterial(SewingMaterial material) {
        boolean exists = sewingMaterialRepository.existsByNameIgnoreCase(material.getName());
        if (exists) {
            throw new DuplicateResourceException("SewingMaterial already exists with name: " + material.getName());
        }
        return sewingMaterialRepository.save(material);
    }

    /**
     * Get material by ID
     */
    @Override
    @Transactional(readOnly = true)
    public SewingMaterial getMaterialById(Long id) {
        return sewingMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SewingMaterial not found with ID: " + id));
    }

    /**
     * Get material by name
     */
    @Override
    @Transactional(readOnly = true)
    public SewingMaterial getMaterialByName(String name) {
        return sewingMaterialRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("SewingMaterial not found with name: " + name));
    }

    /**
     * Update existing material by ID
     */
    @Override
    public SewingMaterial updateMaterial(Long id, SewingMaterial updatedMaterial) {
        SewingMaterial existing = sewingMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SewingMaterial not found with ID: " + id));

        // Check if name already exists (excluding current record)
        boolean nameExists = sewingMaterialRepository.existsByNameIgnoreCaseAndIdNot(updatedMaterial.getName(), id);
        if (nameExists) {
            throw new DuplicateResourceException("Another SewingMaterial already exists with name: " + updatedMaterial.getName());
        }

        existing.setName(updatedMaterial.getName());
        existing.setPrice(updatedMaterial.getPrice());

        return sewingMaterialRepository.save(existing);
    }

    /**
     * Delete material by ID
     */
    @Override
    public void deleteMaterial(Long id) {
        SewingMaterial material = sewingMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SewingMaterial not found with ID: " + id));

        sewingMaterialRepository.delete(material);
    }

    /**
     * Get all materials
     */
    @Override
    @Transactional(readOnly = true)
    public List<SewingMaterial> getAllMaterials() {
        return sewingMaterialRepository.findAll();
    }
}
