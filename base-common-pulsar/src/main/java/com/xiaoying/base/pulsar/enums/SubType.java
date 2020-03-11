package com.xiaoying.base.pulsar.enums;

public enum SubType {


    /**
     * 独占模式 只有一台机器可以消费
     */
    Exclusive(1),

    /**
     * 共享模式 多台机器同时消费
     */
    Shared(2),

    /**
     * 主备模式
     */
    Failover(3);

    private int subType;

    SubType(int subType) {
        this.subType = subType;
    }



}
