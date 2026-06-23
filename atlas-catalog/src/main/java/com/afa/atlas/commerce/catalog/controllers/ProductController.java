package com.afa.atlas.commerce.catalog.controllers;

import com.afa.atlas.commerce.catalog.controllers.internal.ControllerConstants;
import com.afa.atlas.commerce.catalog.dto.ProductDto;
import com.afa.atlas.commerce.catalog.dto.ProductFilter;
import com.afa.atlas.commerce.catalog.dto.ProductSaveRequest;
import com.afa.atlas.commerce.catalog.dto.ProductsResponse;
import com.afa.atlas.commerce.catalog.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.afa.atlas.commerce.catalog.controllers.internal.ControllerConstants.PRODUCTS;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(PRODUCTS)
@Tag(name = "Products controller", description = "Operations pertaining to...")
public class ProductController {

    private final ProductService service;

    @GetMapping
    @Operation(summary = "Find products by filter")
    public ResponseEntity<ProductsResponse> findAll(
            @Valid @ModelAttribute final ProductFilter filter,
            final Pageable pageable) {

        return ResponseEntity.ok(new ProductsResponse(service.findAll(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable final UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({ControllerConstants.ROLE_ADMIN})
    public ProductDto create(@Valid @RequestBody final ProductSaveRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Secured({ControllerConstants.ROLE_ADMIN})
    public ProductDto update(
            @PathVariable final UUID id,
            @Valid @RequestBody final ProductSaveRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ControllerConstants.ROLE_ADMIN})
    public void delete(@PathVariable final UUID id) {
        service.delete(id);
    }
}
