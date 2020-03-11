package pulsar;

import com.xiaoying.base.pulsar.annotation.pulsar.PulsarMessageConsumer;
import com.xiaoying.base.pulsar.core.PulsarMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-07 13:49
 */
@Configuration
@PulsarMessageConsumer(topic = "123456789",namespace = "default",tenant = "public")
@Slf4j
public  class Pulsarlistener implements PulsarMessageListener<TestClass> {

    @Override
    public void received(TestClass msg) {
        System.out.println(new String(msg.getA()));
        String a=msg.toString();
        log.info(a);
//        throw new RuntimeException("123");
//        Thread th=Thread.currentThread();
//
//        System.out.println("Tread name:"+th.getName());
//
//        log.error("Tread name:"+th.getName());
    }
}