package com.afa.atlas.commerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AtlasOrderApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AtlasOrderApplication.class, args);
    }

}
