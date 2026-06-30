package com.afa.atlas.observability.bfpp;

import com.afa.atlas.observability.annotation.AtlasObservedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static com.afa.atlas.observability.AtlasObservabilityAttributes.OBSERVED_BEAN;

public class AtlasObservabilityBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(AtlasObservabilityBeanFactoryPostProcessor.class);

    private static final String AUDITED_ANNOTATION = AtlasObservedService.class.getName();

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) {
        for (final String beanName : beanFactory.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

            if (!(beanDefinition instanceof AnnotatedBeanDefinition annotatedBeanDefinition)) {
                continue;
            }

            final boolean audited = annotatedBeanDefinition.getMetadata().hasAnnotation(AUDITED_ANNOTATION);

            if (audited) {
                beanDefinition.setAttribute(OBSERVED_BEAN, Boolean.TRUE);
                log.info("I. Atlas audit attribute has been added to bean definition '{}': {}",
                        beanName,
                        beanDefinition.getBeanClassName());
            }
        }
    }
}