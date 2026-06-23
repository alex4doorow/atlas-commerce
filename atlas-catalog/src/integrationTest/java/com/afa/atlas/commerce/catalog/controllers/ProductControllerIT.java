package com.afa.atlas.commerce.catalog.controllers;

import com.afa.atlas.commerce.catalog.controllers.internal.ControllerConstants;
import com.afa.atlas.commerce.catalog.services.ProductImageStorageService;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@ActiveProfiles("it")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private ProductImageStorageService productImageStorageService;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("atlas_catalog_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    @SuppressWarnings({"PMD.UnitTestShouldIncludeAssert"})
    void shouldCreateProduct() throws Exception {
        final String requestBody = """
                {
                  "sku": "it-10001",
                  "brand": "Apple",
                  "name": "Integration Test Product",
                  "description": "Created from integration test",
                  "price": 1000.00,
                  "quantity": 10,
                  "active": true
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post(ControllerConstants.PRODUCTS)
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void shouldCreateAndGetProductById() throws Exception {

        final String requestBody = """
                {
                  "sku": "it-10002",
                  "brand": "Apple",
                  "name": "Integration Test Product",
                  "description": "Created from integration test",
                  "price": 1000.00,
                  "quantity": 10,
                  "active": true
                }
                """;

        final MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post(ControllerConstants.PRODUCTS)
                                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sku").value("it-10002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Integration Test Product"))
                .andReturn();

        final String response = createResult.getResponse().getContentAsString();
        final String id = JsonPath.read(response, "$.id");
        Assertions.assertThat(id).isNotBlank();

        mockMvc.perform(MockMvcRequestBuilders.get(ControllerConstants.PRODUCTS + "/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sku").value("it-10002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Integration Test Product"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(1000.00))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.active").value(true));
    }
}