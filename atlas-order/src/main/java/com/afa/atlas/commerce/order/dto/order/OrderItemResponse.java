package com.afa.atlas.commerce.order.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String sku,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal lineAmount
) {
}