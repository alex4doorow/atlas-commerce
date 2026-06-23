package com.afa.atlas.commerce.analytics.kafka;

import com.afa.atlas.commerce.analytics.constants.KafkaTopics;
import com.afa.atlas.commerce.analytics.services.DailyOrderStatService;
import com.afa.atlas.commerce.common.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventConsumer {

    private final DailyOrderStatService dailyOrderStatService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "atlas-analytics"
    )
    public void consume(final OrderCreatedEvent event) {
        log.info("Order created event received: {}", event);
        dailyOrderStatService.apply(event);
    }
}
