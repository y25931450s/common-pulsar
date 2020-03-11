package com.xiaoying.base.pulsar.enums;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-06 19:46
 */
public enum Cluster {
    HZ("hz"),
    XJP("xjp"),
    US("us"),
    FLKF("flkf");

    private String cluster;

    Cluster(String clusterName) {
        this.cluster = clusterName;
    }

    public String getCluster() {
        return cluster;
    }
}
