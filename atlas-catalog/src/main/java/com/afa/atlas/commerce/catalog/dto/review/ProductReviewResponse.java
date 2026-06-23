package com.afa.atlas.commerce.catalog.dto.review;

import java.time.Instant;
import java.util.List;

public record ProductReviewResponse(
        String id,
        String productId,
        String authorName,
        Integer rating,
        String title,
        String text,
        List<String> pros,
        List<String> cons,
        String status,
        Instant createdAt
) {
}