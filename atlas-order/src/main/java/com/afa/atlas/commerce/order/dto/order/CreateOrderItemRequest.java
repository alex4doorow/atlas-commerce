package com.afa.atlas.commerce.order.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemRequest(
        UUID productId,
        BigDecimal price,
        Integer quantity
) {
}
