package com.xiaoying.base.pulsar.annotation.pulsar;

import com.xiaoying.base.pulsar.annotation.autoconfig.PulsarConsumerRegistrar;
import com.xiaoying.base.pulsar.annotation.autoconfig.PulsarProducerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 13:57
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PulsarProducerRegistrar.class)
@Documented
public @interface EnablePulsarProducer {
}
