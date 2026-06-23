package com.afa.atlas.commerce.search.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSearchResult(
        UUID id,
        String sku,
        String brand,
        String name,
        String description,
        BigDecimal price,
        Boolean active
) {
}
