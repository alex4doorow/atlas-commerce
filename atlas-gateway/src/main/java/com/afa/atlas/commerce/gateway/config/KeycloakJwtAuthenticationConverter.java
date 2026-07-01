package com.afa.atlas.commerce.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    private final ReactiveJwtAuthenticationConverterAdapter delegate =
            new ReactiveJwtAuthenticationConverterAdapter(jwt -> {
                final var converter =
                        new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();

                converter.setJwtGrantedAuthoritiesConverter(KeycloakJwtAuthenticationConverter::extractAuthorities);

                return converter.convert(jwt);
            });

    @Override
    public Mono<AbstractAuthenticationToken> convert(final Jwt jwt) {
        return delegate.convert(jwt);
    }

    private static Collection<GrantedAuthority> extractAuthorities(final Jwt jwt) {
        final Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);

        if (realmAccess == null || !realmAccess.containsKey(ROLES)) {
            return List.of();
        }

        final Object rolesObject = realmAccess.get(ROLES);

        if (!(rolesObject instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> ROLE_PREFIX + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());
    }
}