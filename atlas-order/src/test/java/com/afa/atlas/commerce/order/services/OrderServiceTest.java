package com.afa.atlas.commerce.order.services;

import com.afa.atlas.commerce.common.enums.OrderStatus;
import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import com.afa.atlas.commerce.order.clients.CatalogClient;
import com.afa.atlas.commerce.order.dto.order.CreateOrderItemRequest;
import com.afa.atlas.commerce.order.dto.order.OrderResponse;
import com.afa.atlas.commerce.order.dto.order.OrderSaveRequest;
import com.afa.atlas.commerce.order.entities.customer.Customer;
import com.afa.atlas.commerce.order.entities.order.Order;
import com.afa.atlas.commerce.order.entities.order.OrderItem;
import com.afa.atlas.commerce.order.kafka.OrderEventProducer;
import com.afa.atlas.commerce.order.mappers.OrderMapper;
import com.afa.atlas.commerce.order.repositories.CustomerRepository;
import com.afa.atlas.commerce.order.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"PMD.UnitTestContainsTooManyAsserts"})
class OrderServiceTest {

    @Mock
    private CustomerRepository customerRepository;

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

    @Mock
    private OrderItemService orderItemService;

    @Test
    void shouldCreateOrder() {

        final UUID productId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();

        final OrderSaveRequest request = OrderSaveRequest.builder()
                .customerId(customerId)
                .items(List.of(new CreateOrderItemRequest(
                        productId,
                        BigDecimal.valueOf(1000),
                        2
                )))
                .build();

        final Customer customer = new Customer();
        customer.setId(customerId);
        customer.setFirstName("Alexey");
        customer.setLastName("Fedorov");
        customer.setEmail("alexey.fedorov@example.com");
        customer.setPhone("+79991234567");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        final OrderResponse response = OrderResponse.builder()
                .id(orderId)
                .orderNumber("ORD-TEST")
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.valueOf(2000))
                .items(List.of())
                .build();

        final OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setProductId(productId);
        item.setSku("sku-001");
        item.setProductName("Test Product");
        item.setPrice(BigDecimal.valueOf(1000));
        item.setQuantity(2);
        item.setLineAmount(BigDecimal.valueOf(2000));

        when(orderItemService.createItems(any(Order.class), eq(request.items())))
                .thenReturn(List.of(item));

        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> {
            final Order order = invocation.getArgument(0);
            order.setCreatedAt(OffsetDateTime.now());
            return order;
        });

        when(mapper.toResponse(any(Order.class))).thenReturn(response);

        final OrderResponse result = service.create(request);

        assertThat(result).isSameAs(response);
        verify(orderItemService).createItems(any(Order.class), eq(request.items()));
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