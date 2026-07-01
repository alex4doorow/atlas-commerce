package com.afa.atlas.commerce.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            final ServerHttpSecurity http,
            final KeycloakJwtAuthenticationConverter jwtAuthenticationConverter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        .pathMatchers("/api/v1/products/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/v1/search/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/v1/orders/**").hasRole("ADMIN")
                        .pathMatchers("/api/v1/customers/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .build();
    }

    @Bean
    public KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter() {
        return new KeycloakJwtAuthenticationConverter();
    }
}