package com.afa.atlas.observability.autoconfigure;

import com.afa.atlas.observability.bfpp.AtlasObservabilityBeanFactoryPostProcessor;
import com.afa.atlas.observability.bpp.AtlasObservabilityBeanPostProcessor;
import com.afa.atlas.observability.properties.AtlasObservabilityProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(AtlasObservabilityProperties.class)
@ConditionalOnProperty(
        prefix = "atlas.observability",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class AtlasObservabilityAutoConfiguration {

    @Bean
    public static AtlasObservabilityBeanFactoryPostProcessor atlasAuditBeanFactoryPostProcessor() {
        return new AtlasObservabilityBeanFactoryPostProcessor();
    }

    @Bean
    public static AtlasObservabilityBeanPostProcessor atlasAuditBeanPostProcessor(
            final ConfigurableListableBeanFactory beanFactory) {
        return new AtlasObservabilityBeanPostProcessor(beanFactory);
    }
}