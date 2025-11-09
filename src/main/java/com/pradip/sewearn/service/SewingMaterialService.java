package com.pradip.sewearn.service;

import com.pradip.sewearn.model.SewingMaterial;

import java.util.List;

public interface SewingMaterialService {

    SewingMaterial createMaterial(SewingMaterial material);
    SewingMaterial getMaterialById(Long id);
    SewingMaterial getMaterialByName(String name);
    SewingMaterial updateMaterial(Long id, SewingMaterial updatedMaterial);
    void deleteMaterial(Long id);
    List<SewingMaterial> getAllMaterials();
}