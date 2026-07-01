package com.afa.atlas.commerce.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent (
        UUID orderId,
        String orderNumber,
        BigDecimal totalAmount,
        String createdAt
) implements AtlasEvent {}
