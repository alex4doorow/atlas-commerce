package com.afa.atlas.commerce.common.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Product info")
public record ProductDto(

        @Schema(description = "Product id")
        UUID id,

        @Schema(description = "Product SKU")
        String sku,

        @Schema(description = "Product name")
        String name,

        @Schema(description = "Product description")
        String description,

        @Schema(description = "Product image URL")
        String imageUrl,

        @Schema(description = "Product price")
        BigDecimal price,

        @Schema(description = "Available quantity")
        Integer quantity,

        @Schema(description = "Product is active")
        Boolean active,

        @Schema(description = "Created date/time")
        OffsetDateTime createdAt,

        @Schema(description = "Updated date/time")
        OffsetDateTime updatedAt) {

}