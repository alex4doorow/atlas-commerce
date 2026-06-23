package com.afa.atlas.commerce.common.dto.errors;

import java.util.List;

public record ValidationErrorResponse(
        String code,
        String message,
        List<FieldViolation> violations
) {
}