package com.afa.atlas.commerce.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
public class GatewayConfig {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Bean
    public WebFilter requestIdFilter() {
        return (exchange, chain) -> {
            final String requestId = UUID.randomUUID().toString();

            final var mutatedExchange = exchange.mutate()
                    .request(request -> request.header(REQUEST_ID_HEADER, requestId))
                    .build();

            mutatedExchange.getResponse().beforeCommit(() -> {
                mutatedExchange.getResponse()
                        .getHeaders()
                        .add(REQUEST_ID_HEADER, requestId);
                return Mono.empty();
            });

            return chain.filter(mutatedExchange);
        };
    }
}