package com.pradip.sewearn.controller;

import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.SewingMaterialRequest;
import com.pradip.sewearn.dto.SewingMaterialResponse;
import com.pradip.sewearn.mapper.SewingMaterialMapper;
import com.pradip.sewearn.model.SewingMaterial;
import com.pradip.sewearn.service.SewingMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Tag(name = "Sewing Materials", description = "APIs for managing sewing materials and their pricing details")
public class SewingMaterialController {

    private final SewingMaterialService sewingMaterialService;

    @Operation(summary = "Create new sewing material")
    @PostMapping
    public ResponseEntity<ApiResponse<SewingMaterialResponse>> createMaterial(
            @Valid @RequestBody SewingMaterialRequest request) {

        SewingMaterial material = sewingMaterialService.createMaterial(
                SewingMaterialMapper.toEntity(request));

        SewingMaterialResponse response = SewingMaterialMapper.toDto(material);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("Material created successfully", HttpStatus.CREATED.value(), response));
    }

    @Operation(summary = "Get all sewing materials")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SewingMaterialResponse>>> getAllMaterials() {
        List<SewingMaterialResponse> materials = sewingMaterialService.getAllMaterials()
                .stream()
                .map(SewingMaterialMapper::toDto)
                .toList();

        return ResponseEntity.ok(ApiResponse.of("Fetched all materials", HttpStatus.OK.value(), materials));
    }

    @Operation(summary = "Get material by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SewingMaterialResponse>> getMaterialById(@PathVariable Long id) {
        SewingMaterial material = sewingMaterialService.getMaterialById(id);
        return ResponseEntity.ok(ApiResponse.of("Material found", HttpStatus.OK.value(),
                SewingMaterialMapper.toDto(material)));
    }

    @Operation(summary = "Get material by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<SewingMaterialResponse>> getMaterialByName(@PathVariable String name) {
        SewingMaterial material = sewingMaterialService.getMaterialByName(name);
        return ResponseEntity.ok(ApiResponse.of("Material found", HttpStatus.OK.value(),
                SewingMaterialMapper.toDto(material)));
    }

    @Operation(summary = "Update sewing material")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SewingMaterialResponse>> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody SewingMaterialRequest request) {

        SewingMaterial updated = sewingMaterialService.updateMaterial(id, SewingMaterialMapper.toEntity(request));
        return ResponseEntity.ok(ApiResponse.of("Material updated successfully", HttpStatus.OK.value(),
                SewingMaterialMapper.toDto(updated)));
    }

    @Operation(summary = "Delete sewing material")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable Long id) {
        sewingMaterialService.deleteMaterial(id);
        return ResponseEntity.ok(ApiResponse.of("Material deleted successfully", HttpStatus.OK.value(), null));
    }
}