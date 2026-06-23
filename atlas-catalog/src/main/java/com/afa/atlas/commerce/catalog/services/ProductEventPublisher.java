package com.afa.atlas.commerce.catalog.services;

import com.afa.atlas.commerce.catalog.config.RabbitConfig;
import com.afa.atlas.commerce.common.enums.AtlasErrorCode;
import com.afa.atlas.commerce.common.events.ProductIndexedEvent;
import com.afa.atlas.commerce.common.exceptions.AtlasException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private static final String PRODUCT_CREATED = "product.created";
    private static final String PRODUCT_UPDATED = "product.updated";
    private static final String PRODUCT_DELETED = "product.deleted";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publishProductCreated(final ProductIndexedEvent event) {
        publish(PRODUCT_CREATED, event);
    }

    public void publishProductUpdated(final ProductIndexedEvent event) {
        publish(PRODUCT_UPDATED, event);
    }

    public void publishProductDeleted(final UUID id) {
        publish(PRODUCT_DELETED, id.toString());
    }

    private void publish(final String routingKey, final Object payload) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.PRODUCT_EXCHANGE,
                    routingKey,
                    objectMapper.writeValueAsString(payload)
            );

        } catch (JacksonException ex) {
            throw new AtlasException(
                    AtlasErrorCode.INTERNAL_ERROR,
                    "Failed to serialize product event",
                    ex
            );
        }
    }
}