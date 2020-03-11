package com.xiaoying.base.pulsar.service;

import com.alibaba.fastjson.JSON;
import com.xiaoying.base.pulsar.config.PulsarConstant;
import com.xiaoying.base.pulsar.core.PulsarMessageListener;
import com.xiaoying.base.pulsar.exception.PulsarErrorMessage;
import com.xiaoying.base.pulsar.exception.PulsarServiceException;
import com.xiaoying.base.pulsar.init.CatTransaction;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;

import org.apache.pulsar.shade.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.pulsar.shade.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 17:12
 */
@Data
@Slf4j
public class PulsarListener implements InitializingBean {

    private Consumer consumer;


    private PulsarMessageListener userMessageListener;


    private MessageListener messageListener;

    private String unRevicedZone;

    private String localZone;

    private ThreadPoolExecutor pulsarListenerExecutor;

    private Class messageType;

    private ObjectMapper objectMapper;

    private String charset = "UTF-8";

    public PulsarListener() {
        //初始化pulsar监听器
        setMessageListener(new MessageListenerWrapper());

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.messageType = getMessageType();
        this.objectMapper=new ObjectMapper();
        log.debug("pulsarMQ messageType: {}", messageType.getName());
    }


    public class MessageListenerWrapper implements MessageListener {


        @Override
        public void received(Consumer consumer, Message msg) {

            pulsarListenerExecutor.execute(() -> {
                CatTransaction transaction = new CatTransaction(PulsarConstant.PULSAR_CONSUMER_TYPE, consumer.getTopic());
                try {
                    if (StringUtils.isEmpty(unRevicedZone) && !localZone.equals(unRevicedZone)) {
                        userMessageListener.received(doConvertMessage(msg));
                    }
                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    log.error(PulsarErrorMessage.RECEIVED_MESSAGE_ERROR.getMessage(), e);
                    transaction.setStatus(e);
                    consumer.negativeAcknowledge(msg);
                } finally {
                    transaction.complete();
                }
            });
        }
    }


    public boolean isConnected() {
        return consumer.isConnected();
    }


    private Object doConvertMessage(Message messageExt) {
        if (Objects.equals(messageType, Message.class)) {
            return messageExt;
        } else {
            String str = new String(messageExt.getData(), Charset.forName(charset));
            if (Objects.equals(messageType, String.class)) {
                return str;
            } else {
                try {
                    // JSON里面包含了实体没有的字段导致反序列化失败,故开启该特性
                    return JSON.parseObject(str,messageType);
//                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                    return objectMapper.readValue(str, messageType);
                } catch (Exception e) {
                    log.info("pulsar convert failed. str:{}, msgType:{}", str, messageType);
                    throw new RuntimeException("cannot convert message to " + messageType, e);
                }
            }
        }
    }

    private Class getMessageType() {
        Class<?> targetClass = userMessageListener.getClass().getSuperclass();

        Class<?> superclass = targetClass.getSuperclass();
        Type[] interfaces = targetClass.getGenericInterfaces();
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)) {
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (Objects.equals(parameterizedType.getRawType(), PulsarMessageListener.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class) actualTypeArguments[0];
                        } else {
                            return Object.class;
                        }
                    }
                }
            }

            return Object.class;
        } else {
            return Object.class;
        }
    }

}
