package com.afa.atlas.commerce.catalog.services;

import com.afa.atlas.commerce.catalog.documents.ProductReviewDocument;
import com.afa.atlas.commerce.catalog.dto.review.ProductReviewResponse;
import com.afa.atlas.commerce.catalog.dto.review.ProductReviewSaveRequest;
import com.afa.atlas.commerce.catalog.dto.review.ProductReviewSummaryResponse;
import com.afa.atlas.commerce.catalog.entities.Product;
import com.afa.atlas.commerce.catalog.mappers.ProductReviewMapper;
import com.afa.atlas.commerce.catalog.repositories.mongo.ProductReviewRepository;
import com.afa.atlas.commerce.common.enums.ProductReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductService productService;
    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewMapper productReviewMapper;

    public ProductReviewResponse create(final UUID productId, final ProductReviewSaveRequest request) {

        final Product product = productService.findByIdOrThrow(productId);
        final ProductReviewDocument document = ProductReviewDocument.builder()
                .productId(product.getId().toString())
                .authorName(request.authorName())
                .rating(request.rating())
                .title(request.title())
                .text(request.text())
                .pros(request.pros())
                .cons(request.cons())
                .status(ProductReviewStatus.PUBLISHED)
                .build();

        return productReviewMapper.toResponse(productReviewRepository.save(document));
    }

    public List<ProductReviewResponse> findByProductId(final UUID productId) {
        final Product product = productService.findByIdOrThrow(productId);

        return productReviewRepository
                .findByProductIdAndStatusOrderByCreatedAtDesc(product.getId().toString(), ProductReviewStatus.PUBLISHED)
                .stream()
                .map(productReviewMapper::toResponse)
                .toList();
    }

    public ProductReviewSummaryResponse getSummary(final UUID productId) {
        final Product product = productService.findByIdOrThrow(productId);

        final List<ProductReviewDocument> reviews = productReviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(
                product.getId().toString(),
                ProductReviewStatus.PUBLISHED);

        final long count = reviews.size();
        final BigDecimal averageRating = count == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(reviews.stream()
                .mapToInt(ProductReviewDocument::getRating)
                .average()
                .orElse(0.0)
        ).setScale(2, RoundingMode.HALF_UP);

        return new ProductReviewSummaryResponse(productId, count, averageRating);
    }
}