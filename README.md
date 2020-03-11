# common-pulsar

背景：

原生的pulsar客户端接入过于复杂，所以针对我们自身的框架，对pulsar客户端进行了封装，方便使用方进行接入。



应用场景：

适用于不同机房之间的同步需，只关注业务逻辑，具体消息的多机房同步和相互同步都交给这个平台，业务方需要做好幂等处理



maven引入：



<dependency>


       <groupId>com.xiaoying</groupId>

       <artifactId>base-common-pulsar</artifactId> 

       <version>1.1.0-RELEASE</version>


</dependency>



使用方式：

Spring boot使用方式
开启消费者功能
@EnablePulsarConsumer

public class ApplicationBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }
}
开启消费者功能后，只需要消费方法实现 com.xiaoying.base.pulsar.core.PulsarMessageListener 接口，并且加上Spring Bean配置，如下所示：

@Configuration
@PulsarMessageConsumer (topic = "demo",tenant = "middle-service",namespace = "u-center")

public class ConsumerListerTest implements PulsarMessageListener<T>
 {
    @Override
    public void received (T msg) {


                   //将消息转换为String类型输出

        System.out.println (msg.toString());

    }
}


如消费失败，框架会捕获异常，并对失败的消息进行否定确定（negativeAcknowledge）,pulsar服务器1分种内会再次向consumer发送消息

（使用方可自定义死信队列失败次数，默认为10次，死信队列topic为  {consumer.topic}&deadLetter）。





topic	订阅主题 （tenant-namespace-topic 唯一确定一个订阅渠道）	是
namespace	命名空间（需提前申请）	是
tenant	租户（需提前申请）	是
unRevicedLocalZone

设置本地是否接受推送 默认接受(如设置不接受则需填入本地zone 如hz,xjp,us,flkf)	否
SubscriptionInitialPosition

消费位置 默认 Earliest	否




开启生产者功能
@EnablePulsarProducer

public class ApplicationBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }
}

然后可直接通过@PulsarMessageProducer引用标注 com.xiaoying.base.pulsar.core.PulsarProducer 自动注入生产者，类里面支持进行同步和异步的消息发送：

@PulsarMessageProducer(topic = "demo", tenant = "middle-service", namespace = "u-center")
private PulsarProducer pulsarProducer;



设置地理复制（默认全球四个大区）
例：复制到US大区



List<Cluster> clusterList=new ArrayList<>();
clusterList.add(Cluster.US);
pulsarProducer.setReplicationClusters(clusterList);









topic	订阅主题 （tenant-namespace-topic 唯一确定一个订阅渠道）	是
namespace	命名空间（需提前申请）	是
tenant	租户（需提前申请）	是






使用方自定义成配置（配置在apollo上）


com.quwei.xiaoying.pulsar.sendTimeoutMs

Producer发送超时时间 默认 30000 ms

com.quwei.xiaoying.pulsar.receiverQueueSize
Consumer设置容纳待处理消息的队列的最大大小 默认 1000

com.quwei.xiaoying.pulsar.ackTimeout
Consumer消息确认超时时间 默认 30000 ms
com.quwei.xiaoying.pulsar.connectionTimeoutMs
Consumer、Producer连接超时时间 默认 10000ms
com.quwei.xiaoying.pulsar.listenerThreads
Consumer 消费核心线程组大小 默认 5
com.quwei.xiaoying.pulsar.maximumPoolSize
Consumer 消费最大线程组大小 默认20




pulsar控制台 

线上  账号 pulsar 密码 pulsar

http://47.96.187.122:9527/#/management/clusters

QA和DEV  账号 pulsar 密码 pulsar

http://172.16.1.182:9531/#/management/clusters

