package com.pradip.sewearn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private int statusCode;
    private T data;

    public static <T> ApiResponse<T> of(String message, int statusCode, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .build();
    }
}
