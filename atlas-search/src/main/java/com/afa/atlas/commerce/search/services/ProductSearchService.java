package com.afa.atlas.commerce.search.services;

import com.afa.atlas.commerce.common.dto.PageResponse;
import com.afa.atlas.commerce.search.documents.ProductSearchDocument;
import com.afa.atlas.commerce.search.dto.ProductSearchResult;
import com.afa.atlas.commerce.search.mappers.ProductSearchMapper;
import com.afa.atlas.commerce.search.repositories.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchMapper mapper;
    private final ProductSearchRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;

    public PageResponse<ProductSearchResult> search(
            final String query,
            final String brand,
            final BigDecimal maxPrice,
            final int page,
            final int size
    ) {

        final Pageable pageable = PageRequest.of(page, size);

        final NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    b.must(m -> m.match(mm -> mm
                            .field("name")
                            .query(query)));

                    if (brand != null && !brand.isBlank()) {
                        b.filter(f -> f.term(t -> t
                                .field("brand")
                                .value(brand)));
                    }

                    if (maxPrice != null) {
                        b.filter(f -> f.range(r -> r
                                .number(n -> n
                                        .field("price")
                                        .lt(maxPrice.doubleValue()))));
                    }

                    b.filter(f -> f.term(t -> t
                            .field("active")
                            .value(true)));

                    return b;
                }))
                .withPageable(pageable)
                .build();

        final SearchHits<ProductSearchDocument> hits =
                elasticsearchOperations.search(searchQuery, ProductSearchDocument.class);

        final List<ProductSearchDocument> documents = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageResponse<>(
                mapper.toDtoList(documents),
                hits.getTotalHits(),
                calculateTotalPages(hits.getTotalHits(), size),
                page,
                size
        );
    }

    public ProductSearchDocument save(final ProductSearchDocument document) {
        return repository.save(document);
    }

    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    private int calculateTotalPages(final long totalElements, final int size) {
        return (int) Math.ceil((double) totalElements / size);
    }
}