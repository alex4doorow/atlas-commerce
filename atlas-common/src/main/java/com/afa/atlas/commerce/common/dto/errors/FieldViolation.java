package com.afa.atlas.commerce.common.dto.errors;

public record FieldViolation(
        String field,
        String message
) {
}
