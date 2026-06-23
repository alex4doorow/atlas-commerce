package com.afa.atlas.commerce.order.clients;

import com.afa.atlas.commerce.common.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "atlas-catalog",
        url = "${clients.catalog.url}"
)
public interface CatalogClient {

    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable("id") UUID id);
}
