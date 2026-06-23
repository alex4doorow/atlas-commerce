package com.afa.atlas.commerce.catalog.repositories.mongo;

import com.afa.atlas.commerce.catalog.documents.ProductReviewDocument;
import com.afa.atlas.commerce.catalog.enums.ProductReviewStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductReviewRepository extends MongoRepository<ProductReviewDocument, String> {

    List<ProductReviewDocument> findByProductIdAndStatusOrderByCreatedAtDesc(
            String productId,
            ProductReviewStatus status
    );

    long countByProductIdAndStatus(String productId, ProductReviewStatus status);
}
