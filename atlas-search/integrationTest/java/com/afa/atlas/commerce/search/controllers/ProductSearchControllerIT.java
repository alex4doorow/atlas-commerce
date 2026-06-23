package com.afa.atlas.commerce.search.controllers;

import com.afa.atlas.commerce.search.documents.ProductSearchDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@ActiveProfiles("it")
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class ProductSearchControllerIT {

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", ELASTICSEARCH::getHttpHostAddress);
    }

    @Container
    static final ElasticsearchContainer ELASTICSEARCH = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:9.0.3")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @DynamicPropertySource
    static void elasticsearchProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", ELASTICSEARCH::getHttpHostAddress);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setUp() {

        final IndexOperations indexOperations = elasticsearchOperations.indexOps(ProductSearchDocument.class);

        if (indexOperations.exists()) {
            indexOperations.delete();
        }

        indexOperations.create();
        indexOperations.putMapping();

        saveProduct("10001",
                "iPhone 15 Pro",
                "Apple smartphone",
                "Apple",
                BigDecimal.valueOf(99999),
                true);
        saveProduct("10002",
                "iPhone 14",
                "Apple smartphone",
                "Apple",
                BigDecimal.valueOf(120000),
                true);
        saveProduct("10003",
                "iPhone SE",
                "Apple compact smartphone",
                "Apple",
                BigDecimal.valueOf(50000),
                false);
        saveProduct("10004",
                "Samsung Galaxy S25",
                "Android smartphone",
                "Samsung",
                BigDecimal.valueOf(90000),
                true);

        indexOperations.refresh();
    }

    @Test
    void shouldSearchActiveAppleIphoneWithPriceLessThan100000() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/search/products")
                        .param("q", "iphone")
                        .param("brand", "Apple")
                        .param("maxPrice", "100000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].sku", Matchers.is("10001")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.is("iPhone 15 Pro")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].brand", Matchers.is("Apple")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].active", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", Matchers.is(10)));
    }

    private void saveProduct(
            final String sku,
            final String name,
            final String description,
            final String brand,
            final BigDecimal price,
            final boolean active
    ) {

        final ProductSearchDocument product = new ProductSearchDocument();
        product.setId(UUID.randomUUID());
        product.setSku(sku);
        product.setName(name);
        product.setDescription(description);
        product.setBrand(brand);
        product.setPrice(price);
        product.setActive(active);

        final IndexQuery query = new IndexQuery();
        query.setId(product.getId().toString());
        query.setObject(product);

        elasticsearchOperations.index(query, IndexCoordinates.of("products"));
    }
}