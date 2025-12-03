package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.RawMaterialTypeRequest;
import com.pradip.sewearn.dto.RawMaterialTypeResponse;
import com.pradip.sewearn.exception.custom.DuplicateResourceException;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.RawMaterialTypeMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.service.RawMaterialTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RawMaterialTypeServiceImpl implements RawMaterialTypeService {

    private final RawMaterialTypeRepository repository;
    private final RawMaterialTypeMapper mapper;

    @Override
    public RawMaterialTypeResponse createMaterial(RawMaterialTypeRequest request) {

        repository.findByNameIgnoreCase(request.getName())
                .ifPresent(m -> {
                    throw new DuplicateResourceException("Material already exists with name: " + request.getName());
                });

        RawMaterialType entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialTypeResponse getMaterialById(Long id) {
        return mapper.toDto(getMaterialEnById(id));
    }

    public RawMaterialType getMaterialEnById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RawMaterialTypeResponse getMaterialByName(String name) {
        return mapper.toDto(getMaterialEnByName(name));
    }

    public RawMaterialType getMaterialEnByName(String name) {
        return repository.findByNameIgnoreCase(name).orElseThrow(() -> new ResourceNotFoundException("Material not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialTypeResponse> getAllMaterials() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public RawMaterialTypeResponse updateMaterial(Long id, RawMaterialTypeRequest request) {

        RawMaterialType existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));

        repository.findByNameIgnoreCase(request.getName())
                .filter(material -> !material.getId().equals(id))
                .ifPresent(m -> {
                    throw new DuplicateResourceException("Material name already exists: " + request.getName());
                });

        mapper.updateEntity(existing, request);
        return mapper.toDto(repository.save(existing));
    }

    @Override
    public void deleteMaterial(Long id) {
        RawMaterialType existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + id));

        repository.delete(existing);
    }

    @Override
    public RawMaterialType getMaterialByIdOrName(Long id, String name) {
        if (id != null && id != 0) return getMaterialEnById(id);
        if (name != null && !name.isBlank()) return getMaterialEnByName(name);

        throw new ResourceNotFoundException("Material id or name must be provided");
    }
}