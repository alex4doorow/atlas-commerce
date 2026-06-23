package com.afa.atlas.commerce.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import com.afa.atlas.commerce.catalog.dto.ProductDto;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${atlas.cache.ttl.hours}")
    private int cacheTtlHours;

    @Bean
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory) {

        final JacksonJsonRedisSerializer<ProductDto> serializer = new JacksonJsonRedisSerializer<>(ProductDto.class);

        final RedisCacheConfiguration configuration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(cacheTtlHours))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .build();
    }
}