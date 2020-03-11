package com.xiaoying.base.pulsar.annotation.autoconfig;

import com.xiaoying.base.pulsar.annotation.pulsar.PulsarMessageConsumer;
import com.xiaoying.base.pulsar.config.PulsarClientConfig;
import com.xiaoying.base.pulsar.enums.SubType;
import com.xiaoying.base.pulsar.exception.PulsarErrorMessage;
import com.xiaoying.base.pulsar.exception.PulsarServiceException;
import com.xiaoying.base.pulsar.config.PulsarConfigProperties;
import com.xiaoying.base.pulsar.service.PulsarListener;
import com.xiaoying.base.pulsar.core.PulsarMessageListener;
import com.xiaoying.base.pulsar.core.PulsarProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.xiaoying.base.pulsar.util.TopicUtils.bulidProduceTopic;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 14:13
 */

@Slf4j
@SuppressWarnings(value = "unchecked")
public class PulsarConsumerCreator implements ApplicationContextAware, EnvironmentAware, SmartInitializingSingleton {

    private ConfigurableApplicationContext applicationContext;

    private AtomicLong counter = new AtomicLong(0);
    private PulsarConfigProperties pulsarConfigProperties = new PulsarConfigProperties();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = (ConfigurableApplicationContext) applicationContext;

    }

    @Override
    public void setEnvironment(Environment environment) {

    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(PulsarMessageConsumer.class);

        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerContainer);
        }
    }


    private void registerContainer(String beanName, Object bean) {
        Class<?> clazz = AopUtils.getTargetClass(bean);
        if (!PulsarMessageListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + PulsarMessageListener.class.getName());
        }

        PulsarMessageConsumer annotation = AnnotationUtils.findAnnotation(clazz, PulsarMessageConsumer.class);

        String containerBeanName = String.format("%s_%s", PulsarProducer.class.getName(),
                counter.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;

        genericApplicationContext.registerBean(containerBeanName, PulsarListener.class,
                () -> createPulsarConsumer(bean, annotation));
        PulsarListener pulsarListener = genericApplicationContext.getBean(containerBeanName,
                PulsarListener.class);


        if (!pulsarListener.isConnected()) {

            throw new PulsarServiceException("consumer connect failed");
        }

        log.info("Register the PulsarListener, PulsarListener:{}", containerBeanName);
    }

    public PulsarListener createPulsarConsumer(Object bean, PulsarMessageConsumer pulsarMessageConsumer) {


        String topic = bulidProduceTopic(pulsarMessageConsumer.topic(), pulsarMessageConsumer.tenant(), pulsarMessageConsumer.namespace());

        PulsarListener pulsarLstener = new PulsarListener();
        //设置死信策略
        DeadLetterPolicy deadLetterPolicy = DeadLetterPolicy.builder().deadLetterTopic(pulsarMessageConsumer.topic() + "&deadLetter").maxRedeliverCount(pulsarConfigProperties.getMaxRedeliverCount()).build();
        //设置用户自定义监听方法
        pulsarLstener.setUserMessageListener((PulsarMessageListener) bean);
        try {
            Consumer consumer = PulsarClientConfig.getPulsarClient().newConsumer()
                    .topic(topic)
                    .subscriptionType(getSubscriptionType(pulsarMessageConsumer.type()))
                    .messageListener(pulsarLstener.getMessageListener())
                    .subscriptionName(pulsarMessageConsumer.topic() + "-subscription")
                    .receiverQueueSize(pulsarConfigProperties.getReceiverQueueSize())
                    .ackTimeout(pulsarConfigProperties.getAckTimeout(), TimeUnit.MILLISECONDS)
                    .deadLetterPolicy(deadLetterPolicy)
                    .subscriptionInitialPosition(pulsarMessageConsumer.consumerPosition())
                    .subscribe();
            pulsarLstener.setConsumer(consumer);

            //设置不接受推送的地区
            pulsarLstener.setUnRevicedZone(pulsarMessageConsumer.unRevicedLocalZone());
            //设置发送消息的地区
            pulsarLstener.setLocalZone(pulsarConfigProperties.getZone());
            //生成监听器线程池
            ThreadPoolExecutor pulsarListenerExecutor=new ThreadPoolExecutor(pulsarConfigProperties.getListenerThreads(),
                    pulsarConfigProperties.getMaximumPoolSize(), 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(pulsarConfigProperties.getReceiverQueueSize()));
            pulsarLstener.setPulsarListenerExecutor(pulsarListenerExecutor);
            //优雅停机
            Runtime.getRuntime().addShutdownHook(
                    new Thread(() ->
                    {
                        try {
                            consumer.close();
                        } catch (Exception e) {
                           log.warn(e.getMessage());
                        }
                        pulsarListenerExecutor.shutdownNow();
                    }
                    ));
        } catch (Exception e) {
            log.error("create consumer fail",e);
            throw new PulsarServiceException(PulsarErrorMessage.CONSUMER_CREATE_ERROR, e);
        }

        return pulsarLstener;

    }


    /**
     * 获取订阅模式
     *
     * @param subType
     * @return
     */
    private SubscriptionType getSubscriptionType(SubType subType) {
        switch (subType) {
            case Exclusive:
                return SubscriptionType.Exclusive;
            case Shared:
                return SubscriptionType.Shared;
            case Failover:
                return SubscriptionType.Failover;


        }
        return SubscriptionType.Shared;

    }




}
