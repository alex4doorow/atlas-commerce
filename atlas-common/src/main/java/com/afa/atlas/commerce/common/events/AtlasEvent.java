package com.afa.atlas.commerce.common.events;

public sealed interface AtlasEvent permits OrderCreatedEvent, ProductIndexedEvent {
}
