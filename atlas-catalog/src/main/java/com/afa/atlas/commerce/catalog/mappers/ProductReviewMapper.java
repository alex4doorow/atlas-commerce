package com.afa.atlas.commerce.catalog.mappers;

import com.afa.atlas.commerce.catalog.documents.ProductReviewDocument;
import com.afa.atlas.commerce.catalog.dto.review.ProductReviewResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    ProductReviewResponse toResponse(ProductReviewDocument document);
}