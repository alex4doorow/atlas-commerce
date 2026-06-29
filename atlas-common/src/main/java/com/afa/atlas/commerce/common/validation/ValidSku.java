package com.afa.atlas.commerce.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SkuValidator.class)
public @interface ValidSku {

    String message() default "SKU must have format PRD-12345";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}