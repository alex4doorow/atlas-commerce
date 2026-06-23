package com.afa.atlas.commerce.search.services;

import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import com.afa.atlas.commerce.common.events.ProductIndexedEvent;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import com.afa.atlas.commerce.search.config.RabbitConfig;
import com.afa.atlas.commerce.search.documents.ProductSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductIndexConsumer {

    private final ProductSearchService productSearchService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.PRODUCT_SEARCH_QUEUE)
    public void consume(
            final String payload,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) final String routingKey
    ) {
        log.info("Received message: routingKey={}, payload={}", routingKey, payload);

        try {
            switch (routingKey) {
                case "product.created", "product.updated" -> {
                    final ProductIndexedEvent event = objectMapper.readValue(payload, ProductIndexedEvent.class);

                    final ProductSearchDocument document = new ProductSearchDocument();
                    document.setId(event.id());
                    document.setSku(event.sku());
                    document.setBrand(event.brand());
                    document.setName(event.name());
                    document.setDescription(event.description());
                    document.setPrice(event.price());
                    document.setActive(event.active());

                    productSearchService.save(document);
                }
                case "product.deleted" -> {
                    final UUID id = UUID.fromString(objectMapper.readValue(payload, String.class));
                    productSearchService.delete(id);
                }
                default -> log.warn("Unknown routingKey: {}", routingKey);
            }
        } catch (JacksonException ex) {
            throw new AtlasException(AtlasErrorCode.INTERNAL_ERROR, "Failed to process product event", ex);
        }
    }
}