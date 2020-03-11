package com.xiaoying.base.pulsar.core;

import org.apache.pulsar.client.api.Message;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 10:41
 */
public interface PulsarMessageListener<T> {


    /**
     * 监听方法
     *
     * @param msg
     */
    void received(T msg);
}
