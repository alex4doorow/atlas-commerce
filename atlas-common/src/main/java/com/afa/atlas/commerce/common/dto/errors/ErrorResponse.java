package com.afa.atlas.commerce.common.dto.errors;

public record ErrorResponse(
        String code,
        String message
) {
}