package com.pradip.sewearn.util;

import com.pradip.sewearn.dto.PagedResponse;
import org.springframework.data.domain.Page;

public class PagingUtils {

    public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.<T>builder()
                .items(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}