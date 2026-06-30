package com.afa.atlas.crypto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atlas.crypto")
public record AtlasCryptoProperties(
        boolean enabled,
        String secret
) {
}