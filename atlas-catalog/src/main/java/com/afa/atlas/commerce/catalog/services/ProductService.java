package com.afa.atlas.commerce.catalog.services;

import com.afa.atlas.commerce.catalog.dto.ProductDto;
import com.afa.atlas.commerce.catalog.dto.ProductFilter;
import com.afa.atlas.commerce.catalog.dto.ProductSaveRequest;
import com.afa.atlas.commerce.catalog.entities.Product;
import com.afa.atlas.commerce.catalog.mappers.ProductMapper;
import com.afa.atlas.commerce.catalog.repositories.ProductRepository;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.afa.atlas.commerce.common.enums.AtlasErrorCode.PRODUCT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductImageStorageService imageStorageService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final ProductEventPublisher productEventPublisher;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll(
            final ProductFilter productFilter,
            Pageable pageable) {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductDto findById(final UUID id) {

        log.debug("CACHE MISS id={}", id);

        final Product product = productRepository.findById(id)
                .orElseThrow(() -> new AtlasException(PRODUCT_NOT_FOUND, "Product not found: %s".formatted(id)));

        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDto create(final ProductSaveRequest request) {

        final Product entity = productMapper.toEntity(request);
        final Product savedProduct = productRepository.save(entity);

        productEventPublisher.publishProductCreated(productMapper.toIndexedEvent(savedProduct));

        return productMapper.toDto(savedProduct);
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public ProductDto update(final UUID id, final ProductSaveRequest request) {

        final Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setActive(request.active());

        final Product savedProduct = productRepository.save(product);

        productEventPublisher.publishProductUpdated(productMapper.toIndexedEvent(savedProduct));

        return productMapper.toDto(savedProduct);
    }

    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void delete(final UUID id) {

        final Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        productRepository.delete(product);

        productEventPublisher.publishProductDeleted(id);
    }

    @Transactional
    public ProductDto uploadImage(final UUID id, final MultipartFile file) throws IOException {
        final Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        final String imageUrl = imageStorageService.upload(id, file);
        product.setImageUrl(imageUrl);

        return productMapper.toDto(product);
    }
}