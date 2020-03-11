package com.xiaoying.base.pulsar.annotation.pulsar;

import java.lang.annotation.*;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 14:16
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PulsarMessageProducer {

    /**
     * topic名
     * @return
     */
    String topic();

    /**
     * 租户
     * @return
     */
    String tenant();

    /**
     * 命名空间
     * @return
     */
    String namespace();





}
