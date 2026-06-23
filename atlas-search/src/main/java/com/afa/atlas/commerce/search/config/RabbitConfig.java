package com.afa.atlas.commerce.search.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PRODUCT_EXCHANGE = "atlas.product.exchange";

    public static final String PRODUCT_SEARCH_QUEUE = "product.search.index";

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(PRODUCT_EXCHANGE);
    }

    @Bean
    public Queue productSearchQueue() {
        return new Queue(PRODUCT_SEARCH_QUEUE);
    }

    @Bean
    public Binding productCreatedBinding(
            final Queue productSearchQueue,
            final TopicExchange productExchange
    ) {
        return BindingBuilder
                .bind(productSearchQueue)
                .to(productExchange)
                .with("product.*");
    }

    @Bean
    public AmqpAdmin amqpAdmin(final ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationRunner rabbitInitializer(
            final AmqpAdmin amqpAdmin,
            final TopicExchange productExchange,
            final Queue productSearchQueue,
            final Binding productCreatedBinding
    ) {
        return args -> {
            amqpAdmin.declareExchange(productExchange);
            amqpAdmin.declareQueue(productSearchQueue);
            amqpAdmin.declareBinding(productCreatedBinding);
        };
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
