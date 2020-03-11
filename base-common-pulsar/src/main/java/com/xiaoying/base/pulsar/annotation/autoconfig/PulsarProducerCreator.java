package com.xiaoying.base.pulsar.annotation.autoconfig;

import com.xiaoying.base.pulsar.annotation.pulsar.PulsarMessageProducer;
import com.xiaoying.base.pulsar.config.PulsarClientConfig;
import com.xiaoying.base.pulsar.core.PulsarProducer;
import com.xiaoying.base.pulsar.config.PulsarConfigProperties;
import com.xiaoying.base.pulsar.exception.PulsarErrorMessage;
import com.xiaoying.base.pulsar.exception.PulsarServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.Schema;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static com.xiaoying.base.pulsar.util.TopicUtils.bulidProduceTopic;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 15:26
 */
@Slf4j
public class PulsarProducerCreator extends PulsarProcess {


    private PulsarConfigProperties pulsarConfigProperties = new PulsarConfigProperties();

    @Override
    protected void processField(Object bean, String beanName, Field field) {

        PulsarMessageProducer pulsarMessageProducer = AnnotationUtils.getAnnotation(field, PulsarMessageProducer.class);
        if (pulsarMessageProducer == null) {
            return;
        }
        PulsarProducer pulsarProducer = createPulsarProducer(pulsarMessageProducer);

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        ReflectionUtils
                .setField(field, bean, pulsarProducer);
        field.setAccessible(accessible);
        log.info("Register the PulsarMessageProducer success, PulsarMessageProducer:{}", beanName);

    }


    private PulsarProducer createPulsarProducer(PulsarMessageProducer pulsarMessageProducer) {

        String topic = bulidProduceTopic(pulsarMessageProducer.topic(),pulsarMessageProducer.tenant(),pulsarMessageProducer.namespace());
        PulsarProducer pulsarProducer = new PulsarProducer();

        try {
            Producer<String> producer = PulsarClientConfig.getPulsarClient().newProducer(Schema.STRING)
                    .topic(topic)
                    .sendTimeout(pulsarConfigProperties.getSendTimeoutMs(), TimeUnit.MILLISECONDS)
                    .blockIfQueueFull(pulsarConfigProperties.isBlockIfQueueFull())
                    .maxPendingMessages(pulsarConfigProperties.getMaxPendingMessages())
                    .maxPendingMessagesAcrossPartitions(pulsarConfigProperties.getMaxPendingMessagesAcrossPartitions())
                    .messageRoutingMode(MessageRoutingMode.RoundRobinPartition)
                    .create();
            pulsarProducer.setProducer(producer);
            //producer优雅停机
            Runtime.getRuntime().addShutdownHook(
                    new Thread(() ->
                    {
                        try {
                            producer.close();
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    }
                    ));
        } catch (Exception e) {
            log.error("create producer fail",e);
            throw new PulsarServiceException(PulsarErrorMessage.PRODUCER_CREATE_ERROR, e);
        }

        return pulsarProducer;

    }


}
