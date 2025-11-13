package com.pradip.sewearn.controller;

import com.pradip.sewearn.contstant.ApiMessages;
import com.pradip.sewearn.dto.ApiResponse;
import com.pradip.sewearn.dto.RawMaterialTypeRequest;
import com.pradip.sewearn.dto.RawMaterialTypeResponse;
import com.pradip.sewearn.service.RawMaterialTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Tag(name = "Raw Materials", description = "Manage raw material types and pricing")
public class RawMaterialTypeController {

    private final RawMaterialTypeService service;

    @Operation(summary = "Create material", description = "Create new raw material type")
    @PostMapping
    public ResponseEntity<ApiResponse<RawMaterialTypeResponse>> create(
            @Valid @RequestBody RawMaterialTypeRequest request) {

        RawMaterialTypeResponse dto = service.createMaterial(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(ApiMessages.MATERIAL_CREATED, dto, 201));
    }

    @Operation(summary = "Get material by id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RawMaterialTypeResponse>> getById(@PathVariable Long id) {
        RawMaterialTypeResponse dto = service.getMaterialById(id);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.MATERIAL_FETCHED, dto));
    }

    @Operation(summary = "Get material by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<RawMaterialTypeResponse>> getByName(@PathVariable String name) {
        RawMaterialTypeResponse dto = service.getMaterialByName(name);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.MATERIAL_FETCHED, dto));
    }

    @Operation(summary = "List all materials")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RawMaterialTypeResponse>>> listAll() {
        List<RawMaterialTypeResponse> list = service.getAllMaterials();
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.MATERIAL_LIST_FETCHED, list));
    }

    @Operation(summary = "Update material")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RawMaterialTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RawMaterialTypeRequest request) {

        RawMaterialTypeResponse dto = service.updateMaterial(id, request);
        return ResponseEntity.ok(ApiResponse.success(ApiMessages.MATERIAL_UPDATED, dto));
    }

    @Operation(summary = "Delete material")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.deleteMaterial(id);
        return ResponseEntity.noContent()
                .build();
    }
}
