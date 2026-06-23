package com.afa.atlas.commerce.order.kafka;

import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import com.afa.atlas.commerce.order.constants.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(final OrderCreatedEvent event) {
        kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event.orderId().toString(),
                event
        );
    }
}