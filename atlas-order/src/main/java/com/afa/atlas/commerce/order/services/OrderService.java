package com.afa.atlas.commerce.order.services;

import com.afa.atlas.commerce.common.dto.PageResponse;
import com.afa.atlas.commerce.common.dto.ProductDto;
import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import com.afa.atlas.commerce.order.clients.CatalogClient;
import com.afa.atlas.commerce.order.dto.CreateOrderItemRequest;
import com.afa.atlas.commerce.order.dto.OrderResponse;
import com.afa.atlas.commerce.order.dto.OrderSaveRequest;
import com.afa.atlas.commerce.order.entities.order.Order;
import com.afa.atlas.commerce.order.entities.order.OrderItem;
import com.afa.atlas.commerce.order.enums.OrderStatus;
import com.afa.atlas.commerce.order.kafka.OrderEventProducer;
import com.afa.atlas.commerce.order.mappers.OrderMapper;
import com.afa.atlas.commerce.order.repositories.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.afa.atlas.commerce.common.enums.AtlasErrorCode.ORDER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderEventProducer orderEventProducer;
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final OrderMapper mapper;

    @Transactional
    public OrderResponse create(final OrderSaveRequest request) {
        final Order order = new Order();

        order.setId(UUID.randomUUID());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.CREATED);

        final List<OrderItem> items = request.items().stream()
                .map(itemRequest -> createOrderItem(order, itemRequest))
                .toList();

        order.setItems(items);

        final BigDecimal totalAmount = items.stream()
                .map(OrderItem::getLineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        final Order savedOrder = orderRepository.saveAndFlush(order);

        orderEventProducer.send(
                new OrderCreatedEvent(
                        savedOrder.getId(),
                        savedOrder.getOrderNumber(),
                        savedOrder.getTotalAmount(),
                        savedOrder.getCreatedAt().toInstant().toString()
                )
        );

        return mapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(final UUID id) {
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AtlasException(ORDER_NOT_FOUND, "Order not found: %s".formatted(id)));

        return mapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAll(final int page, final int size) {
        final Page<OrderResponse> result = orderRepository
                .findAll(PageRequest.of(page, size))
                .map(mapper::toResponse);

        return new PageResponse<>(
                result.getContent(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    private OrderItem createOrderItem(
            final Order order,
            final CreateOrderItemRequest request) {
        final OrderItem item = new OrderItem();

        item.setId(UUID.randomUUID());
        item.setOrder(order);

        final ProductDto product;
        try {
            product = catalogClient.getProductById(request.productId());
        } catch (FeignException.NotFound ex) {
            throw new AtlasException(AtlasErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + request.productId(), ex);
        }

        item.setProductId(product.id());
        item.setSku(product.sku());
        item.setProductName(product.name());

        item.setPrice(request.price());
        item.setQuantity(request.quantity());

        final BigDecimal lineAmount = request.price().multiply(BigDecimal.valueOf(request.quantity()));

        item.setLineAmount(lineAmount);

        return item;
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
