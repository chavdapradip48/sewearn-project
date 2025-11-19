package com.pradip.sewearn.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MarkAsCompletedRequest {
    private LocalDate completedDate;
}