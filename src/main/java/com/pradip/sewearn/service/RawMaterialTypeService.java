package com.pradip.sewearn.service;

import com.pradip.sewearn.dto.RawMaterialTypeRequest;
import com.pradip.sewearn.dto.RawMaterialTypeResponse;

import java.util.List;

public interface RawMaterialTypeService {

    RawMaterialTypeResponse createMaterial(RawMaterialTypeRequest request);

    RawMaterialTypeResponse getMaterialById(Long id);

    RawMaterialTypeResponse getMaterialByName(String name);

    List<RawMaterialTypeResponse> getAllMaterials();

    RawMaterialTypeResponse updateMaterial(Long id, RawMaterialTypeRequest request);

    void deleteMaterial(Long id);
}