package com.afa.atlas.commerce.catalog.controllers.internal;

import com.afa.atlas.commerce.common.dto.errors.ErrorResponse;
import com.afa.atlas.commerce.common.dto.errors.FieldViolation;
import com.afa.atlas.commerce.common.dto.errors.ValidationErrorResponse;
import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class CatalogGlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NoResourceFoundException ex) {
        return new ErrorResponse(
                AtlasErrorCode.NOT_FOUND.name(),
                "Resource not found"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handle(final MethodArgumentNotValidException ex) {

        final List<FieldViolation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldViolation(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return new ValidationErrorResponse(
                AtlasErrorCode.VALIDATION_ERROR.name(),
                "Validation failed",
                violations
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final EntityNotFoundException ex) {
        return new ErrorResponse(
                AtlasErrorCode.PRODUCT_NOT_FOUND.name(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(AtlasException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final AtlasException ex) {
        return new ErrorResponse(
                ex.getErrorCode().name(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Exception ex) {
        return new ErrorResponse(
                AtlasErrorCode.INTERNAL_ERROR.name(),
                ex.getMessage()
        );
    }
}
