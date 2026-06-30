package com.afa.atlas.commerce.order.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.hibernate.SpringBeanContainer;

@Configuration
public class HibernateBeanContainerConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            final ConfigurableListableBeanFactory beanFactory
    ) {
        return properties -> properties.put(
                AvailableSettings.BEAN_CONTAINER,
                new SpringBeanContainer(beanFactory)
        );
    }
}
