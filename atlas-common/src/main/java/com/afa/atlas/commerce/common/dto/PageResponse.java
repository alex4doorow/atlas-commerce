package com.afa.atlas.commerce.common.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        long totalElements,
        int totalPages,
        int page,
        int size
) {
}
