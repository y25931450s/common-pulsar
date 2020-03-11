package com.xiaoying.base.pulsar.exception;

/**
 * @author yushuo
 */
public class PulsarErrorMessage implements ErrorMessage {
    public static final PulsarErrorMessage OK = new PulsarErrorMessage("[base-common-pulsar] ok");
    public static final PulsarErrorMessage CONSUMER_CREATE_ERROR = new PulsarErrorMessage("[base-common-pulsar] consumer create fail !");
    public static final PulsarErrorMessage CONSUMER_CLOSE_ERROR = new PulsarErrorMessage("[base-common-pulsar] consumer close fail !");
    public static final PulsarErrorMessage ANNOTATION_TOPIC_IS_NULL = new PulsarErrorMessage("[base-common-pulsar] topic_tenant_namespace can not be null ！");
    public static final PulsarErrorMessage PRODUCER_CREATE_ERROR = new PulsarErrorMessage("[base-common-pulsar] producer create fail !");
    public static final PulsarErrorMessage CLIENT_CREATE_ERROR = new PulsarErrorMessage("[base-common-pulsar] client create fail !");
    public static final PulsarErrorMessage GET_APOLLO_CONFIG_ERROR = new PulsarErrorMessage("[base-common-pulsar] get apollo config fail!");
    public static final PulsarErrorMessage RECEIVED_MESSAGE_ERROR = new PulsarErrorMessage("[base-common-pulsar] received message error!");


    private String message;


    protected PulsarErrorMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
