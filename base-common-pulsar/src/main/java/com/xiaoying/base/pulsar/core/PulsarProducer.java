package com.xiaoying.base.pulsar.core;

import com.alibaba.fastjson.JSONObject;
import com.xiaoying.base.pulsar.config.PulsarConstant;
import com.xiaoying.base.pulsar.enums.Cluster;
import com.xiaoying.base.pulsar.init.CatTransaction;
import lombok.Data;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 14:09
 */
@Data
public class PulsarProducer {


    private Producer<String> producer;



    private List<String> restrictReplicationTo = Arrays.asList(
            Cluster.HZ.getCluster(),Cluster.XJP.getCluster(),Cluster.US.getCluster(), Cluster.FLKF.getCluster()
    );

    /**
     * 发送消息
     *
     * @param message
     * @return
     */
    public MessageId sendMessage(Object message) {
        CatTransaction transaction = new CatTransaction(PulsarConstant.PULSAR_PRODUCE_TYPE, producer.getTopic());
        String jsonMessage= JSONObject.toJSONString(message);
        try {
            return producer.newMessage()
                    .value(jsonMessage)
                    .replicationClusters(restrictReplicationTo)
                    .send();
        } catch (Exception e) {
            transaction.setStatus(e);
            throw new RuntimeException(e);
        } finally {
            transaction.complete();
        }
    }

    /**
     * 异步发送消息
     *
     * @param message
     * @return
     */
    public CompletableFuture<MessageId> sendMessageAsync(Object message) {

        CatTransaction transaction = new CatTransaction(PulsarConstant.PULSAR_PRODUCE_TYPE, producer.getTopic());
        String jsonMessage= JSONObject.toJSONString(message);
        try {
            return producer.newMessage()
                    .value(jsonMessage)
                    .replicationClusters(restrictReplicationTo)
                    .sendAsync();
        } catch (Exception e) {
            transaction.setStatus(e);
            throw new RuntimeException(e);
        } finally {
            transaction.complete();
        }
    }

    /**
     * 检测producer下的topic是否已经存在
     *
     * @return
     */
    public boolean isExist() {

        return producer.getLastSequenceId() > 0;

    }

    /**
     * 设置地理复制区域 默认全球
     *
     * @param clusterList
     */
    public void setReplicationClusters(List<Cluster> clusterList) {

        if (!StringUtils.isEmpty(clusterList)) {

            this.restrictReplicationTo = new ArrayList<>(this.restrictReplicationTo);
            this.restrictReplicationTo.clear();
            for (Cluster item : clusterList) {
                this.restrictReplicationTo.add(item.getCluster());
            }
        }

    }



}
