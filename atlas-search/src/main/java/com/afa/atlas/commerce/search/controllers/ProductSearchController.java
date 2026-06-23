package com.afa.atlas.commerce.search.controllers;

import com.afa.atlas.commerce.common.dto.PageResponse;
import com.afa.atlas.commerce.search.controllers.internal.ControllerConstants;
import com.afa.atlas.commerce.search.documents.ProductSearchDocument;
import com.afa.atlas.commerce.search.dto.ProductSearchResult;
import com.afa.atlas.commerce.search.services.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(ControllerConstants.PRODUCTS)
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService service;

    @GetMapping()
    public PageResponse<ProductSearchResult> searchProducts(
            @RequestParam final String q,
            @RequestParam(required = false) final String brand,
            @RequestParam(required = false) final BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        return service.search(q, brand, maxPrice, page, size);
    }

    @PostMapping
    public ResponseEntity<ProductSearchDocument> save(@RequestBody final ProductSearchDocument document) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.save(document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}