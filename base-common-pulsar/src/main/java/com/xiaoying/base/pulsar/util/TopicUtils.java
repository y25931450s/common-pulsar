package com.xiaoying.base.pulsar.util;

import com.xiaoying.base.pulsar.annotation.pulsar.PulsarMessageProducer;
import com.xiaoying.base.pulsar.exception.PulsarErrorMessage;
import com.xiaoying.base.pulsar.exception.PulsarServiceException;
import org.springframework.util.StringUtils;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-22 13:54
 */
public class TopicUtils {
    public static String bulidProduceTopic(String topic,String tenant,String namesapce){

        if (StringUtils.isEmpty(topic)||StringUtils.isEmpty(tenant)||StringUtils.isEmpty(namesapce)) {
            throw new PulsarServiceException(PulsarErrorMessage.ANNOTATION_TOPIC_IS_NULL);
        }
        String url="persistent://%s/%s/%s";
        return String.format(url,tenant,namesapce,topic);
    }

}
