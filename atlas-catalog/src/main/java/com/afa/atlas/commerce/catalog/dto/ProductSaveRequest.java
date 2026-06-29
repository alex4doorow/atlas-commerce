package com.afa.atlas.commerce.catalog.dto;

import com.afa.atlas.commerce.common.validation.ValidSku;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Product save request")
public record ProductSaveRequest(

        @ValidSku
        @NotBlank
        @Size(max = 64)
        @Schema(description = "Product SKU")
        String sku,

        @Size(max = 64)
        @Schema(description = "Product brand")
        String brand,

        @NotBlank
        @Size(max = 255)
        @Schema(description = "Product name")
        String name,

        @Schema(description = "Product description")
        String description,

        @Size(max = 500)
        @Schema(description = "Product image URL")
        String imageUrl,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Product price")
        BigDecimal price,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Available quantity")
        Integer quantity,

        @NotNull
        @Schema(description = "Product is active")
        Boolean active

) {
}