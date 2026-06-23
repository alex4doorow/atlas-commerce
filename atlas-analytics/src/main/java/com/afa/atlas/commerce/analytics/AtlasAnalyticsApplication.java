package com.afa.atlas.commerce.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class AtlasAnalyticsApplication {
    public static void main(final String[] args) {
        SpringApplication.run(AtlasAnalyticsApplication.class, args);
    }
}
