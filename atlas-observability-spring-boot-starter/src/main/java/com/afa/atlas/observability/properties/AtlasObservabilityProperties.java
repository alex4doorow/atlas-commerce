package com.afa.atlas.observability.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atlas.observability")
public record AtlasObservabilityProperties(boolean enabled) {
}
