package com.afa.atlas.commerce.common.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidSkuTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldAcceptValidSku() {
        final TestDto dto = new TestDto("PRD-10001");

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldRejectInvalidSku() {
        final TestDto dto = new TestDto("10001");

        assertThat(validator.validate(dto)).hasSize(1);
    }

    @Test
    void shouldIgnoreNullBecauseNotBlankIsSeparateValidation() {
        final TestDto dto = new TestDto(null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    private record TestDto(@ValidSku String sku) {
    }
}