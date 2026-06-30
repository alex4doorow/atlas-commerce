package com.afa.atlas.observability.bpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static com.afa.atlas.observability.AtlasObservabilityAttributes.OBSERVED_BEAN;

public class AtlasObservabilityBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(AtlasObservabilityBeanPostProcessor.class);

    private final ConfigurableListableBeanFactory beanFactory;

    public AtlasObservabilityBeanPostProcessor(final ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (isObserved(beanName)) {
            log.info("II. Observed bean '{}' has been initialized", beanName);
        }

        return bean;
    }

    private boolean isObserved(final String beanName) {
        if (!beanFactory.containsBeanDefinition(beanName)) {
            return false;
        }

        final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        return Boolean.TRUE.equals(beanDefinition.getAttribute(OBSERVED_BEAN));
    }
}
