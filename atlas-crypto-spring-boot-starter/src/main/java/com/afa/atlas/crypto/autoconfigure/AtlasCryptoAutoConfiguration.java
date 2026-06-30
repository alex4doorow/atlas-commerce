package com.afa.atlas.crypto.autoconfigure;

import com.afa.atlas.crypto.properties.AtlasCryptoProperties;
import com.afa.atlas.crypto.service.AesGcmCryptoService;
import com.afa.atlas.crypto.service.CryptoService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(AtlasCryptoProperties.class)
@ConditionalOnProperty(
        prefix = "atlas.crypto",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class AtlasCryptoAutoConfiguration {

    @Bean
    public CryptoService cryptoService(final AtlasCryptoProperties properties) {
        return new AesGcmCryptoService(properties.secret());
    }
}