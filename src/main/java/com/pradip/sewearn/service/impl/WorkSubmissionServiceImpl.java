package com.pradip.sewearn.service.impl;

import com.pradip.sewearn.dto.WorkSubmissionRequest;
import com.pradip.sewearn.dto.WorkSubmissionSummaryResponse;
import com.pradip.sewearn.exception.custom.ResourceNotFoundException;
import com.pradip.sewearn.model.SewingMaterial;
import com.pradip.sewearn.model.WorkItem;
import com.pradip.sewearn.model.WorkSubmission;
import com.pradip.sewearn.repository.SewingMaterialRepository;
import com.pradip.sewearn.repository.WorkSubmissionRepository;
import com.pradip.sewearn.service.WorkSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkSubmissionServiceImpl implements WorkSubmissionService {

    private final WorkSubmissionRepository submissionRepository;
    private final SewingMaterialRepository materialRepository;

    @Override
    public WorkSubmission createSubmission(WorkSubmissionRequest request) {
        WorkSubmission submission = WorkSubmission.builder()
                .submissionDate(request.getSubmissionDate())
                .items(new ArrayList<>())
                .totalEarning(0.0)
                .build();

        double total = buildItemsForSubmission(submission, request);
        submission.setTotalEarning(total);

        return submissionRepository.save(submission);
    }

    @Override
    public WorkSubmission updateSubmission(Long id, WorkSubmissionRequest request) {
        WorkSubmission existing = getSubmissionById(id);
        existing.getItems().clear();
        existing.setSubmissionDate(request.getSubmissionDate());

        double total = buildItemsForSubmission(existing, request);
        existing.setTotalEarning(total);

        return submissionRepository.save(existing);
    }

    @Override
    public WorkSubmission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + id));
    }

    @Override
    public List<WorkSubmission> getAllSubmissions() {
        return submissionRepository.findAllByOrderBySubmissionDateDesc();
    }

    @Override
    public void deleteSubmission(Long id) {
        WorkSubmission existing = getSubmissionById(id);
        submissionRepository.delete(existing);
    }

    @Override
    public List<WorkSubmission> getSubmissionsByDate(LocalDate date) {
        return submissionRepository.findBySubmissionDate(date);
    }

    @Override
    public List<WorkSubmissionSummaryResponse> getAllSubmissionsSummary() {
        return submissionRepository.findAllByOrderBySubmissionDateDesc().stream()
                .map(sub -> WorkSubmissionSummaryResponse.builder()
                        .submissionDate(sub.getSubmissionDate())
                        .totalItems(sub.getItems().stream().mapToInt(WorkItem::getQuantity).sum())
                        .totalEarning(sub.getTotalEarning())
                        .build()
                )
                .toList();
    }

    // Helper method to build WorkItems and calculate total
    private double buildItemsForSubmission(WorkSubmission submission, WorkSubmissionRequest request) {
        double total = 0.0;

        for (var itemReq : request.getItems()) {
            SewingMaterial material = resolveMaterial(itemReq.getMaterialId(), itemReq.getMaterialName());

            double itemTotal = material.getPrice() * itemReq.getQuantity();

            WorkItem item = WorkItem.builder()
                    .material(material)
                    .quantity(itemReq.getQuantity())
                    .totalPrice(itemTotal)
                    .submission(submission)
                    .build();

            submission.addItem(item);
            total += itemTotal;
        }

        return total;
    }

    // Helper method to find material by ID or name
    private SewingMaterial resolveMaterial(Long materialId, String materialName) {
        if (materialId != null) {
            return materialRepository.findById(materialId)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with ID: " + materialId));
        } else if (materialName != null) {
            return materialRepository.findByNameIgnoreCase(materialName)
                    .orElseThrow(() -> new ResourceNotFoundException("Material not found with name: " + materialName));
        } else {
            throw new ResourceNotFoundException("Either materialId or materialName must be provided.");
        }
    }
}