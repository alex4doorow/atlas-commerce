package com.afa.atlas.commerce.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SkuValidator implements ConstraintValidator<ValidSku, String> {

    private static final Pattern SKU_PATTERN = Pattern.compile("^PRD-\\d+$");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return SKU_PATTERN.matcher(value).matches();
    }
}
