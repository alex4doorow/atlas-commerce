package com.afa.atlas.commerce.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product filter")
public record ProductFilter(

        @Schema(description = "Product name contains", example = "iPhone")
        String nameContext,

        @Schema(description = "Product SKU", example = "IPH-15-PRO")
        String sku

) {
}