package com.afa.atlas.commerce.catalog.controllers;

import com.afa.atlas.commerce.catalog.dto.review.ProductReviewResponse;
import com.afa.atlas.commerce.catalog.dto.review.ProductReviewSaveRequest;
import com.afa.atlas.commerce.catalog.dto.review.ProductReviewSummaryResponse;
import com.afa.atlas.commerce.catalog.services.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.afa.atlas.commerce.catalog.controllers.internal.ControllerConstants.PRODUCTS;

@RestController
@RequiredArgsConstructor
@RequestMapping(PRODUCTS + "/{productId}/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductReviewResponse create(
            @PathVariable final UUID productId,
            @Valid @RequestBody final ProductReviewSaveRequest request
    ) {
        return productReviewService.create(productId, request);
    }

    @GetMapping
    public List<ProductReviewResponse> findByProductId(@PathVariable final UUID productId) {
        return productReviewService.findByProductId(productId);
    }

    @GetMapping("/summary")
    public ProductReviewSummaryResponse getSummary(@PathVariable final UUID productId) {
        return productReviewService.getSummary(productId);
    }
}