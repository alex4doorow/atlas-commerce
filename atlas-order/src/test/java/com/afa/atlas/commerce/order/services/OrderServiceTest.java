package com.afa.atlas.commerce.order.services;

import com.afa.atlas.commerce.common.dto.ProductDto;
import com.afa.atlas.commerce.common.enums.OrderStatus;
import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import com.afa.atlas.commerce.order.clients.CatalogClient;
import com.afa.atlas.commerce.order.dto.CreateOrderItemRequest;
import com.afa.atlas.commerce.order.dto.OrderResponse;
import com.afa.atlas.commerce.order.dto.OrderSaveRequest;
import com.afa.atlas.commerce.order.entities.order.Order;
import com.afa.atlas.commerce.order.kafka.OrderEventProducer;
import com.afa.atlas.commerce.order.mappers.OrderMapper;
import com.afa.atlas.commerce.order.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts"})
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService service;

    @Test
    void shouldCreateOrder() {

        final UUID productId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        final OrderSaveRequest request = OrderSaveRequest.builder()
                .items(List.of(new CreateOrderItemRequest(
                        productId,
                        BigDecimal.valueOf(1000),
                        2
                )))
                .build();

        final ProductDto product = ProductDto.builder()
                .id(productId)
                .sku("sku-001")
                .name("Test Product")
                .price(BigDecimal.valueOf(1000))
                .quantity(10)
                .active(true)
                .build();

        final OrderResponse response = OrderResponse.builder()
                .id(orderId)
                .orderNumber("ORD-TEST")
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.valueOf(2000))
                .items(List.of())
                .build();

        when(catalogClient.getProductById(productId)).thenReturn(product);
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            final Order order = invocation.getArgument(0);
            order.setCreatedAt(OffsetDateTime.now());
            return order;
        });

        when(mapper.toResponse(any(Order.class))).thenReturn(response);

        final OrderResponse result = service.create(request);

        assertThat(result).isSameAs(response);
        verify(catalogClient).getProductById(productId);
        verify(orderRepository).saveAndFlush(argThat(order ->
                order.getId() != null
                        && order.getOrderNumber() != null
                        && order.getOrderNumber().startsWith("ORD-")
                        && order.getStatus() == OrderStatus.CREATED
                        && BigDecimal.valueOf(2000).compareTo(order.getTotalAmount()) == 0
                        && order.getItems().size() == 1
                        && order.getItems().getFirst().getProductId().equals(productId)
                        && "sku-001".equals(order.getItems().getFirst().getSku())
                        && "Test Product".equals(order.getItems().getFirst().getProductName())
                        && order.getItems().getFirst().getQuantity().equals(2)
                        && BigDecimal.valueOf(1000).compareTo(order.getItems().getFirst().getPrice()) == 0
                        && BigDecimal.valueOf(2000).compareTo(order.getItems().getFirst().getLineAmount()) == 0
        ));

        verify(mapper).toResponse(any(Order.class));
        verify(orderEventProducer).send(any(OrderCreatedEvent.class));

        verifyNoMoreInteractions(catalogClient, orderRepository, mapper, orderEventProducer);
    }
}