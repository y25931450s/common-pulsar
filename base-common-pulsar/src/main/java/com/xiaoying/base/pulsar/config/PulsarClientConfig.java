package com.xiaoying.base.pulsar.config;


import com.xiaoying.base.pulsar.exception.PulsarErrorMessage;
import com.xiaoying.base.pulsar.exception.PulsarServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-05 13:50
 */
@Slf4j
public class PulsarClientConfig {


    private static PulsarConfigProperties pulsarConfigProperties = new PulsarConfigProperties();
    private static final String SERVER_URL = pulsarConfigProperties.getServiceUrl();

    private volatile static PulsarClient pulsarClient;


    public static PulsarClient getPulsarClient() {

        try {
            pulsarClient = PulsarClient.builder()
                    .serviceUrl(SERVER_URL)
                    .enableTcpNoDelay(true)
                    .connectionTimeout(pulsarConfigProperties.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS)
                    .build();
        } catch (Exception e) {
            throw new PulsarServiceException(PulsarErrorMessage.CLIENT_CREATE_ERROR, e);
        }


        return pulsarClient;
    }

}
