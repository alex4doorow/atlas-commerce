package com.afa.atlas.commerce.catalog.dto.review;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductReviewSummaryResponse(
        UUID productId,
        long reviewCount,
        BigDecimal averageRating
) {
}