package com.afa.atlas.commerce.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductIndexedEvent (
        UUID id,
        String sku,
        String brand,
        String name,
        String description,
        BigDecimal price,
        Boolean active
) implements AtlasEvent {}