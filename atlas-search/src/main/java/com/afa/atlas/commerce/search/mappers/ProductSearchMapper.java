package com.afa.atlas.commerce.search.mappers;

import com.afa.atlas.commerce.search.documents.ProductSearchDocument;
import com.afa.atlas.commerce.search.dto.ProductSearchResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductSearchMapper {

    ProductSearchResult toDto(ProductSearchDocument document);
    List<ProductSearchResult> toDtoList(List<ProductSearchDocument> documents);
}
