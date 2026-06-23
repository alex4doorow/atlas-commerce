package com.afa.atlas.commerce.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemRequest(
        UUID productId,
        BigDecimal price,
        Integer quantity
) {
}
