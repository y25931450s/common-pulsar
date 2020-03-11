package com.xiaoying.base.pulsar.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.xiaoying.base.pulsar.exception.PulsarErrorMessage;
import com.xiaoying.base.pulsar.exception.PulsarServiceException;
import lombok.Data;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yushuo
 * @date 2019/8/30 16:23
 */
@Data
@Component
public class PulsarConfigProperties {

    public PulsarConfigProperties() {

        initPulsarConfigProperties();
        initPulsarClientProperties();
    }

    /**
     * 连接地址
     */
    private String serviceUrl;

    /**
     * 地区
     */
    private String zone;
    /**
     * client连接时间
     */

    private Integer connectionTimeoutMs;

    /**
     * Producer发送超时时间
     */
    private Integer sendTimeoutMs = 30000;
    /**
     * Producer设置当传出消息队列已满时，Producer.send(T)和Producer.sendAsync(T)操作是否应阻塞
     */
    private boolean blockIfQueueFull = false;

    /**
     * Producer设置容纳待处理消息的队列的最大大小
     */
    private int maxPendingMessages = 1000;
    /**
     * Producer设置所有分区中的最大待处理消息数
     */
    private int maxPendingMessagesAcrossPartitions = 50000;


    /**
     * Consumer设置容纳待处理消息的队列的最大大小
     */
    private int receiverQueueSize = 1000;

    /**
     * Consumer确认消息超时时间
     */
    private Long ackTimeout = 30000L;



    /**
     * 用于处理消息侦听器的线程数
     */
     private Integer listenerThreads=5;

    /**
     * 线程池最大线程数
     */
    private Integer maximumPoolSize=20;

    /**
     * 死信队列次数
     */
    private  Integer maxRedeliverCount=10;





    /**
     * 从apollo获取配置
     */
    private void initPulsarClientProperties() {
        try {
            Config config = ConfigService.getConfig("server.pulsar");
            this.serviceUrl = config.getProperty("com.quwei.xiaoying.pulsar.serviceurl", "pulsar://172.16.1.182:6650");
            this.zone = config.getProperty("com.quwei.xiaoying.pulsar.zone", "xjp");

        } catch (Exception e) {
            throw new PulsarServiceException(PulsarErrorMessage.GET_APOLLO_CONFIG_ERROR, e);
        }

    }
    /**
     * 从使用方apollo获取配置
     */
    private void initPulsarConfigProperties(){
        try {
            Config config = ConfigService.getAppConfig();
            this.sendTimeoutMs = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.sendTimeoutMs", "30000"));
            this.maxPendingMessages = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.maxPendingMessages", "1000"));
            this.maxPendingMessagesAcrossPartitions = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.maxPendingMessagesAcrossPartitions", "50000"));
            this.receiverQueueSize = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.receiverQueueSize", "1000"));
            this.ackTimeout = Long.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.ackTimeout", "30000"));
            this.connectionTimeoutMs = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.connectionTimeoutMs", "10000"));
            this.listenerThreads = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.listenerThreads", "5"));
            this.maximumPoolSize = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.maximumPoolSize", "20"));
            this.maxRedeliverCount = Integer.valueOf(config.getProperty("com.quwei.xiaoying.pulsar.maxRedeliverCount", "10"));
        }catch (Exception e) {
            throw new PulsarServiceException(PulsarErrorMessage.GET_APOLLO_CONFIG_ERROR, e);
        }
    }


}
