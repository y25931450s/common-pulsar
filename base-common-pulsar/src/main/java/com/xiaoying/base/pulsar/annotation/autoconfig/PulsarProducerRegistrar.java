package com.xiaoying.base.pulsar.annotation.autoconfig;

import com.xiaoying.base.pulsar.config.PulsarClientConfig;
import com.xiaoying.base.pulsar.config.PulsarConstant;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-06 15:45
 */
public class PulsarProducerRegistrar implements ImportBeanDefinitionRegistrar {
    private void initProducerCreatorBean(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(PulsarConstant.PULSAR_PRODUCER_CREATOR)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(PulsarProducerCreator.class);
            beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(PulsarConstant.PULSAR_PRODUCER_CREATOR,
                    beanDefinitionBuilder.getBeanDefinition());

        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        initProducerCreatorBean(registry);
    }
}
