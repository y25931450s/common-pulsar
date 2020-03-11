package pulsar;

import com.alibaba.fastjson.JSONObject;
import com.xiaoying.base.pulsar.annotation.pulsar.EnablePulsarConsumer;
import com.xiaoying.base.pulsar.annotation.pulsar.EnablePulsarProducer;
import com.xiaoying.base.pulsar.annotation.pulsar.PulsarMessageProducer;
import com.xiaoying.base.pulsar.config.PulsarConfigProperties;
import com.xiaoying.base.pulsar.core.PulsarProducer;
import com.xiaoying.base.pulsar.enums.Cluster;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yushuo
 * @version 1.0
 * @date 2019-11-06 14:40
 */
@RunWith(SpringRunner.class)
@EnablePulsarConsumer
@EnablePulsarProducer
@Import({Pulsarlistener.class,PulsarConfigProperties.class})


public class PulsarTest {
    PulsarProducer pulsarProducer;
    private static final int THREAD_COUNT = 100;

    @Configuration
    public static class PulsarService {
        @PulsarMessageProducer(topic = "123456789",namespace = "default",tenant = "public")
        static PulsarProducer pulsarProducer;

        @PulsarMessageProducer(topic = "123456789",namespace = "default",tenant = "public")
        static PulsarProducer pulsarProducer2;


    }


//    @Configuration
//    @PulsarMessageListener(topic="yushuo")
//    public static class Pulsarlistenr  implements PulsarMessageListenr {
//
//        @Override
//        public void received(Message msg) {
//            System.out.println(new String(msg.getData()));
//        }
//    }

    @Test
    public void PulsarsendMessage() {

//        for(int i=0;i<100;i++)
//        {
//            pulsarProducer.sendMessage("My message yushuo"+i);
//        }

        PulsarProducer pulsarProducer=PulsarService.pulsarProducer;
        this.pulsarProducer=pulsarProducer;
        Runnable runnable = new MyRunnable();
        Runnable runnable2 = new MyRunnable2();
        Thread thread = new Thread(runnable);
        Thread thread2 = new Thread(runnable2);
        thread.start();
        thread2.start();


        int a=1;
    }


    @Test
    public void PulsarsendMessageAsync() {
        List<Cluster> clusterList=new ArrayList<>();
        clusterList.add(Cluster.US);
        PulsarProducer pulsarProducer=PulsarService.pulsarProducer;
        PulsarProducer pulsarProducer2=PulsarService.pulsarProducer2;

        TestClass testClass =new TestClass();
        testClass.setA("yushuo");
        testClass.setB("hello");
            pulsarProducer.setReplicationClusters(clusterList);

            pulsarProducer2.sendMessageAsync(testClass);


        int a=1;
        while (true);
    }
@Test
public void redistestThreadJunit() {
    try {
        // Runner数组，相当于并发多少个
        TestRunnable[] trs = new TestRunnable[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            trs[i] = new TestRunnable() {
                @Override
                public void runTest() throws Throwable {
                    PulsarService.pulsarProducer.sendMessage("yushuo");
                }
            };
        }
        // 用于执行多线程测试用例的Runner，将前面定义的单个Runner组成的数组传入
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
        // 并发执行数组里定义的内容
        mttr.runTestRunnables();
    } catch (Throwable e) {
        e.printStackTrace();
    }

    //避免程序提前结束
    while (true) ;

}
    public class MyRunnable implements Runnable {
        @Override
        public void run() {
            for(int i=0;i<100;i++)
            {
                pulsarProducer.sendMessage("My message yushuo"+i);
            }
        }
    }
    public class MyRunnable2 implements Runnable {
        @Override
        public void run() {
            for(int i=0;i<100;i++)
            {
                pulsarProducer.sendMessage("My message yushuo"+i);
            }
        }
    }

}
