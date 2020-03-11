package com.xiaoying.base.pulsar.annotation.autoconfig;

import com.xiaoying.base.pulsar.config.PulsarClientConfig;
import com.xiaoying.base.pulsar.config.PulsarConstant;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class PulsarConsumerRegistrar implements ImportBeanDefinitionRegistrar {

    private void initConsumerCreatorBean(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(PulsarConstant.PULSAR_CONSUMER_CREATOR)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(PulsarConsumerCreator.class);
            beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(PulsarConstant.PULSAR_CONSUMER_CREATOR,
                    beanDefinitionBuilder.getBeanDefinition());

        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        initConsumerCreatorBean(registry);
    }
}
