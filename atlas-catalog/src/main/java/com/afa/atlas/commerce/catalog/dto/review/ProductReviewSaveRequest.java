package com.afa.atlas.commerce.catalog.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductReviewSaveRequest(

        @NotBlank
        String authorName,

        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,

        @NotBlank
        String title,

        @NotBlank
        String text,

        List<String> pros,

        List<String> cons
) {
}