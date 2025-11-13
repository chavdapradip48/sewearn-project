package com.pradip.sewearn.service.submit;


import com.pradip.sewearn.dto.submit.SubmitItemCreateRequest;
import com.pradip.sewearn.dto.submit.SubmitItemResponse;
import com.pradip.sewearn.dto.submit.SubmitItemUpdateRequest;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.SubmitItemMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.submit.SewEarnSubmit;
import com.pradip.sewearn.model.submit.SubmitItem;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.repository.SewEarnSubmitRepository;
import com.pradip.sewearn.repository.SubmitItemRepository;
import com.pradip.sewearn.service.SubmitItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmitItemServiceImpl implements SubmitItemService {

    private final SubmitItemRepository submitItemRepository;
    private final SewEarnSubmitRepository submitRepository;
    private final RawMaterialTypeRepository materialRepository;

    private final SubmitItemMapper mapper;


    // =====================================
    // CREATE
    // =====================================
    @Override
    public SubmitItemResponse createSubmitItem(SubmitItemCreateRequest request) {

        SewEarnSubmit parent = submitRepository.findById(request.getSubmitId())
                .orElseThrow(() -> new ResourceNotFoundException("SewEarnSubmit not found with id: " + request.getSubmitId()));

        RawMaterialType material = resolveMaterial(request.getMaterialId(), request.getMaterialName());

        SubmitItem item = mapper.toEntity(request);
        item.setRawMaterialType(material);

        double earning = material.getPrice() * item.getQuantity();
        item.setTotalEarning(earning);

        parent.addSubmittedItem(item);

        parent.setTotalQuantity(parent.getTotalQuantity() + item.getQuantity());
        parent.setTotalEarning(parent.getTotalEarning() + earning);

        submitItemRepository.save(item);

        return mapper.toDto(item);
    }


    // =====================================
    // READ
    // =====================================
    @Override
    @Transactional(readOnly = true)
    public SubmitItemResponse getSubmitItemById(Long id) {
        SubmitItem item = submitItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubmitItem not found with id: " + id));

        return mapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmitItemResponse> getItemsBySubmitId(Long submitId) {
        return submitItemRepository.findBySubmitId(submitId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }


    // =====================================
    // UPDATE
    // =====================================
    @Override
    public SubmitItemResponse updateSubmitItem(Long id, SubmitItemUpdateRequest request) {

        SubmitItem existing = submitItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubmitItem not found with id: " + id));

        SewEarnSubmit parent = existing.getSubmit();

        int oldQty = existing.getQuantity();
        double oldEarning = existing.getTotalEarning();

        RawMaterialType material = resolveMaterial(request.getMaterialId(), request.getMaterialName());

        mapper.updateEntity(existing, request);
        existing.setRawMaterialType(material);

        double newEarning = material.getPrice() * existing.getQuantity();
        existing.setTotalEarning(newEarning);

        // Update totals using delta calculations
        parent.setTotalQuantity(parent.getTotalQuantity() - oldQty + existing.getQuantity());
        parent.setTotalEarning(parent.getTotalEarning() - oldEarning + newEarning);

        submitItemRepository.save(existing);

        return mapper.toDto(existing);
    }


    // =====================================
    // DELETE
    // =====================================
    @Override
    public void deleteSubmitItem(Long id) {
        SubmitItem existing = submitItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubmitItem not found with id: " + id));

        SewEarnSubmit parent = existing.getSubmit();

        parent.setTotalQuantity(parent.getTotalQuantity() - existing.getQuantity());
        parent.setTotalEarning(parent.getTotalEarning() - existing.getTotalEarning());

        submitItemRepository.delete(existing);
    }


    // =====================================
    // Helper
    // =====================================
    private RawMaterialType resolveMaterial(Long id, String name) {
        if (id != null) {
            return materialRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with ID: " + id));
        }
        if (name != null && !name.isBlank()) {
            return materialRepository.findByNameIgnoreCase(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with name: " + name));
        }
        throw new ResourceNotFoundException("Material id or name must be provided");
    }
}
