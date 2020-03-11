package com.xiaoying.base.pulsar.annotation.pulsar;

import com.xiaoying.base.pulsar.enums.SubType;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;

import java.lang.annotation.*;

import static org.apache.pulsar.client.api.SubscriptionInitialPosition.Earliest;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 13:53
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PulsarMessageConsumer {
    /**
     * topic名
     * @return
     */
    String topic();

    /**
     * 订阅模式 1.独占模式 2.共享模式
     * @return
     */
    SubType type() default SubType.Shared;

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

    /**
     * 设置本地不接受消息(需要填写本地Zone hz,xjp,us,flkf)
     */

    String unRevicedLocalZone()default "";

    /**
     * 设置开始消费的位置（默认从头开始）
     */

    SubscriptionInitialPosition consumerPosition() default Earliest;


}
