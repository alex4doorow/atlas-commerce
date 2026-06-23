package com.afa.atlas.commerce.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Products response")
public record ProductsResponse(

        @Schema(description = "List of products")
        List<ProductDto> items
) {
}
