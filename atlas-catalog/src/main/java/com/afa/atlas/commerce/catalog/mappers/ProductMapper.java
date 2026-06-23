package com.afa.atlas.commerce.catalog.mappers;

import com.afa.atlas.commerce.catalog.dto.ProductDto;
import com.afa.atlas.commerce.catalog.dto.ProductSaveRequest;
import com.afa.atlas.commerce.catalog.entities.Product;
import com.afa.atlas.commerce.common.events.ProductIndexedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductMapper {

    ProductDto toDto(Product entity);
    List<ProductDto> toDtoList(List<Product> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductSaveRequest request);

    ProductIndexedEvent toIndexedEvent(Product product);
}