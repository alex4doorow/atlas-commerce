package com.afa.atlas.commerce.order.services;

import com.afa.atlas.commerce.common.annotation.OrderAudit;
import com.afa.atlas.commerce.common.dto.PageResponse;
import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import com.afa.atlas.commerce.common.enums.OrderOperation;
import com.afa.atlas.commerce.common.enums.OrderStatus;
import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import com.afa.atlas.commerce.order.dto.order.OrderResponse;
import com.afa.atlas.commerce.order.dto.order.OrderSaveRequest;
import com.afa.atlas.commerce.order.entities.customer.Customer;
import com.afa.atlas.commerce.order.entities.order.Order;
import com.afa.atlas.commerce.order.entities.order.OrderItem;
import com.afa.atlas.commerce.order.kafka.OrderEventProducer;
import com.afa.atlas.commerce.order.mappers.OrderMapper;
import com.afa.atlas.commerce.order.repositories.CustomerRepository;
import com.afa.atlas.commerce.order.repositories.OrderRepository;
import com.afa.atlas.observability.annotation.AtlasObservedService;
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

@AtlasObservedService
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderEventProducer orderEventProducer;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper mapper;
    private final OrderItemService orderItemService;

    @OrderAudit(operation = OrderOperation.CREATE)
    @Transactional
    public OrderResponse create(final OrderSaveRequest request) {

        final Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new AtlasException(AtlasErrorCode.CUSTOMER_NOT_FOUND,
                        "Customer not found: %s".formatted(request.customerId())
                ));

        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.CREATED);
        order.setCustomer(customer);

        final List<OrderItem> items = orderItemService.createItems(order, request.items());
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

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
