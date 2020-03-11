package com.xiaoying.base.pulsar.annotation.pulsar;

import com.xiaoying.base.pulsar.annotation.autoconfig.PulsarConsumerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 11:09
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PulsarConsumerRegistrar.class)
@Documented
public @interface EnablePulsarConsumer   {
}
