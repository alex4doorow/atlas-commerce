package com.afa.atlas.commerce.order.services;

import com.afa.atlas.commerce.common.dto.ProductDto;
import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import com.afa.atlas.commerce.order.clients.CatalogClient;
import com.afa.atlas.commerce.order.dto.order.CreateOrderItemRequest;
import com.afa.atlas.commerce.order.entities.order.Order;
import com.afa.atlas.commerce.order.entities.order.OrderItem;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final CatalogClient catalogClient;

    public List<OrderItem> createItems(
            final Order order,
            final List<CreateOrderItemRequest> requests) {

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            final List<Future<OrderItem>> futures = requests.stream()
                    .map(request -> executor.submit(() -> createItem(order, request)))
                    .toList();

            return futures.stream()
                    .map(this::getResult)
                    .toList();
        }
    }

    private OrderItem createItem(
            final Order order,
            final CreateOrderItemRequest request) {

        final ProductDto product = getProduct(request.productId());

        final OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setOrder(order);

        item.setProductId(product.id());
        item.setSku(product.sku());
        item.setProductName(product.name());

        item.setPrice(request.price());
        item.setQuantity(request.quantity());

        final BigDecimal lineAmount = request.price().multiply(BigDecimal.valueOf(request.quantity()));
        item.setLineAmount(lineAmount);

        return item;
    }

    private ProductDto getProduct(final UUID productId) {
        try {
            return catalogClient.getProductById(productId);
        } catch (FeignException.NotFound ex) {
            throw new AtlasException(AtlasErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + productId,ex);
        }
    }

    private OrderItem getResult(final Future<OrderItem> future) {
        try {
            return future.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AtlasException(AtlasErrorCode.INTERNAL_ERROR, "Order item creation was interrupted", ex);
        } catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof AtlasException atlasException) {
                throw new AtlasException(atlasException.getErrorCode(), atlasException.getMessage(), ex);
            }
            throw new AtlasException(AtlasErrorCode.INTERNAL_ERROR, "Order item creation failed", ex);
        }
    }
}