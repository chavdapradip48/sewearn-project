package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.submit.*;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.mapper.SewEarnSubmitMapper;
import com.pradip.sewearn.model.RawMaterialType;
import com.pradip.sewearn.model.submit.SewEarnSubmit;
import com.pradip.sewearn.model.submit.SubmitItem;
import com.pradip.sewearn.repository.RawMaterialTypeRepository;
import com.pradip.sewearn.repository.SewEarnSubmitRepository;
import com.pradip.sewearn.repository.SubmitItemRepository;
import com.pradip.sewearn.service.SewEarnSubmitService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SewEarnSubmitServiceImpl implements SewEarnSubmitService {

    private final SewEarnSubmitRepository submitRepository;
    private final SubmitItemRepository itemRepository;
    private final RawMaterialTypeRepository materialRepository;

    private final SewEarnSubmitMapper mapper;


    @Override
    public SewEarnSubmitResponse createSubmission(SewEarnSubmitRequest request) {

        SewEarnSubmit submit = mapper.toEntity(request);

        int totalQty = 0;
        long totalEarning = 0L;

        for (SubmitItemRequest itemReq : request.getSubmittedItems()) {

            RawMaterialType material = resolveMaterial(itemReq.getMaterialId(), itemReq.getMaterialName());

            SubmitItem item = mapper.toItemEntity(itemReq);
            item.setRawMaterialType(material);
            item.setTotalEarning(material.getPrice() * itemReq.getQuantity());

            submit.addSubmittedItem(item);

            totalQty += item.getQuantity();
            totalEarning += item.getTotalEarning();
        }

        submit.setTotalQuantity(totalQty);
        submit.setTotalEarning(totalEarning);

        return mapper.toDto(submitRepository.save(submit));
    }


    @Override
    @Transactional(readOnly = true)
    public SewEarnSubmitResponse getSubmissionById(Long id) {
        return mapper.toDto(
                submitRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Submit not found with id: " + id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitResponse> getAllSubmissions(Pageable pageable) {
        return submitRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitResponse> getSubmissionsByDate(LocalDate date, Pageable pageable) {
        return submitRepository.findBySubmissionDate(date, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SewEarnSubmitResponse> getSubmissionsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return submitRepository.findBySubmissionDateBetween(start, end, pageable)
                .map(mapper::toDto);
    }

    @Override
    public SewEarnSubmitResponse updateSubmission(Long id, SewEarnSubmitRequest request) {

        SewEarnSubmit existing = submitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submit not found with id: " + id));

        // Reset data
        existing.getSubmittedItems().clear();
        existing.setSubmissionDate(request.getSubmissionDate());

        int totalQty = 0;
        long totalEarning = 0L;

        for (SubmitItemRequest itemReq : request.getSubmittedItems()) {

            RawMaterialType material = resolveMaterial(itemReq.getMaterialId(), itemReq.getMaterialName());

            SubmitItem item = mapper.toItemEntity(itemReq);
            item.setRawMaterialType(material);
            item.setTotalEarning(material.getPrice() * itemReq.getQuantity());

            existing.addSubmittedItem(item);

            totalQty += item.getQuantity();
            totalEarning += item.getTotalEarning();
        }

        existing.setTotalQuantity(totalQty);
        existing.setTotalEarning(totalEarning);

        return mapper.toDto(submitRepository.save(existing));
    }

    @Override
    public void deleteSubmission(Long id) {
        submitRepository.delete(
                submitRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Submit not found with id: " + id))
        );
    }


    // =========================
    // Helper
    // =========================
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


    @Override
    public List<SewEarnSubmitSummaryResponse> getAllSubmissionsSummary() {
        return submitRepository.findAll()
                .stream()
                .map(mapper::toSummary)
                .toList();
    }

    @Override
    public Page<SewEarnSubmitSummaryResponse> getAllSubmissionsSummaryPaged(Pageable pageable) {
        return submitRepository.findAll(pageable)
                .map(mapper::toSummary);
    }

    @Override
    public Page<SewEarnSubmitSummaryResponse> getSubmissionsByDateSummary(LocalDate date, Pageable pageable) {
        return submitRepository.findBySubmissionDate(date, pageable)
                .map(mapper::toSummary);
    }
}
