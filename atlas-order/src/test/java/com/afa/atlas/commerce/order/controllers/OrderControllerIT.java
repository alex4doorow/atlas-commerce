package com.afa.atlas.commerce.order.controllers;

import com.afa.atlas.commerce.order.kafka.OrderEventProducer;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.afa.atlas.commerce.common.dto.ProductDto;
import com.afa.atlas.commerce.order.clients.CatalogClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;

@Slf4j
@ActiveProfiles("it")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert"})
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogClient catalogClient;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private OrderEventProducer orderEventProducer;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("atlas_order_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void shouldCreateAndGetOrderById() throws Exception {

        final UUID productId = UUID.fromString("19372cef-68bf-4832-8f9c-c2a2dba85a05");

        when(catalogClient.getProductById(productId))
                .thenReturn(ProductDto.builder()
                        .id(productId)
                        .sku("it-order-001")
                        .name("Order Test Product")
                        .description("Product for order integration test")
                        .price(BigDecimal.valueOf(1000))
                        .quantity(100)
                        .active(true)
                        .build());

        final String orderBody = """
                {
                  "items": [
                    {
                      "productId": "%s",
                      "price": 1000.00,
                      "quantity": 2
                    }
                  ]
                }
                """.formatted(productId);

        final MvcResult orderResult = mockMvc.perform(post("/api/v1/orders")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(2000))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andReturn();

        final String orderId = JsonPath.read(
                orderResult.getResponse().getContentAsString(),
                "$.id"
        );

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(2000))
                .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
                .andExpect(jsonPath("$.items[0].sku").value("it-order-001"))
                .andExpect(jsonPath("$.items[0].productName").value("Order Test Product"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(1000))
                .andExpect(jsonPath("$.items[0].lineAmount").value(2000));
    }

    @Test
    void shouldReturnOrdersPage() throws Exception {

        final UUID productId = UUID.fromString("29372cef-68bf-4832-8f9c-c2a2dba85a05");

        when(catalogClient.getProductById(productId))
                .thenReturn(ProductDto.builder()
                        .id(productId)
                        .sku("it-order-page-001")
                        .name("Order Page Test Product")
                        .description("Product for order page integration test")
                        .price(BigDecimal.valueOf(500))
                        .quantity(100)
                        .active(true)
                        .build());

        final String orderBody = """
                {
                  "items": [
                    {
                      "productId": "%s",
                      "price": 500.00,
                      "quantity": 2
                    }
                  ]
                }
                """.formatted(productId);

        mockMvc.perform(post("/api/v1/orders")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }
}