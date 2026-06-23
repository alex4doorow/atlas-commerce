package com.afa.atlas.commerce.search.repositories;

import com.afa.atlas.commerce.search.documents.ProductSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, UUID> {

    Page<ProductSearchDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name,
            String description,
            Pageable pageable
    );
}