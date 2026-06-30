package com.afa.atlas.commerce.order.controllers;

import com.afa.atlas.commerce.order.dto.customer.CustomerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("it")
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private com.afa.atlas.commerce.order.kafka.OrderEventProducer orderEventProducer;

    @Test
    void create_shouldReturnCreatedCustomer() throws Exception {

        final String email = "alexey.fedorov.%s@example.com".formatted(UUID.randomUUID());
        final String body = """
        {
          "firstName": "Alexey",
          "lastName": "Fedorov",
          "email": "%s",
          "phone": "+79991234567"
        }
        """.formatted(email);

        final MvcResult result = mockMvc.perform(post("/api/v1/customers")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value("+79991234567"))
                .andReturn();

        log.info("result : {}", result.getResponse().getContentAsString());
        final CustomerResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CustomerResponse.class
        );

        final UUID customerId = response.id();
        final String dbEmail = jdbcTemplate.queryForObject("select email from customers where id = ?",
                String.class,
                customerId
        );
        assertNotEquals(email, dbEmail);
    }

}