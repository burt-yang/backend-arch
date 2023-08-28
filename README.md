1、spring aop 实现方法入参以及耗时日志 - LoggingFilter
2、全局处理spring mvc框架异常、spring security异常、自定义异常、spring validation异常 - GlobalExceptionHandler
3、jwt filter实现token校验以及spring security认证 TokenAuthenticationFilter
4、全局响应体转换 ResponseBodyFormatter
5、方法级别的入参校验，以及方法级别的权限校验 
6、jackson java中的null到json中的空转换 JacksonConfig
7、mybatais generator插件配置，自动生成Serializable、toString、覆盖mapper.xml文件、Equals和HashCode生成、Model流式赋值方法、SelectOneByExample、ModelColumn关联、批量插入
8、mybatis-spring 一级缓存会失效，加上@transactional注解，一级缓存会生效，没有事物多次select会重新创建多次session。实际生产应该禁用第一第二级缓存，采用redis缓存
9、redis实现数据缓存: 集成spring cache，@cacheable key生成策略 userInfo::SimpleKey [arg1,arg2] RedisConfig

10、redis实现api限流：使用令牌桶、和lua脚本实现限流。 @RateLimiter
    问题1：大量读请求涌入redis，导致redis压力大
    解决1：采用内存限流外加+redis分布式限流方案，降低redis并发量
11、redis分布式锁：使用redisson实现分布式锁，RedLock向不止一个redis master发送请求，超过半数以上算数 @DistributedLock
12、redis和数据库不一致方案：先更新数据库，后删除缓存，延时2s（读的api响应时间），再次删除缓存
    问题1：被动性更新，可能会出现缓存失效，大量请求涌入数据库
    解决1：在缓存失效的时候，采用等待读，即如果之前有请求没命中缓存，并且去读取数据库，则第二请求排队等候第一次读完之后，从缓存中读取第一次的结果

    A_get_data
    redis_cache_miss
    A_get_db
    B_update_db
    B_rm_redis
    (此时如果拿db是b值，但是redis没有值)
    A_update_redis
13、数据排行榜、点赞好友关系储存、队列、延时操作
14、抽象注解缓存获取类，实现在反射时获取注解信息，运行时只需从内存获取，提高效率
15、redis持久化，持久化会影响redis的性能，能不开启就不开启，两种方式：
    1、rdb 通过在redis.config中配置save 900 1生效，多个save配置同时生效，save方法会堵塞客户端的操作，生产环境不采用，采用最多的是bgsave，bgsave会fork子进程来备份
    2、aof append only file

16、redis击穿、雪崩
17、接口幂等，消息队列幂等
18、



websocket

future:
zookeeper
dubbo
message queue

源码：
Spring,Spring MVC,Sprint Boot,Spring Cloud
Tomcat
Mybatis
Hibernate
Dubbo
Zookeeper
Message Queue
ES
canal
sharding-jdbc分库分表
Mysql高可用方案MHA: 主从切换、主从复制、master选举协议


三级缓存流程DefaultSingletonBeanRegistry

一级缓存，singletonObjects，存储所有已创建完毕的单例 Bean （完整的 Bean）
二级缓存，earlySingletonObjects，存储所有仅完成实例化，但还未进行属性注入和初始化的 Bean
三级缓存，singletonFactories，存储能建立这个 Bean 的一个工厂，通过工厂能获取这个 Bean，延迟化 Bean 的生成，工厂生成的 Bean 会塞入二级缓存
Set集合，singletonsCurrentlyInCreation，bean正在在创建中的集合

getBean(A)
1.1.AnnotationConfigServletWebServerApplicationContext AbstractApplicationContext getBean()
1.2.DefaultListableBeanFactory getBean() -> resolveBean() -> resolveNamedBean()
1.3.AbstractBeanFactory getBean() -> doGetBean() -> getSingleton()
1.4.DefaultSingletonBeanRegistry getSingleton(beanName) 返回null (singletonObjects 空，earlySingletonObjects空，singletonFactories空)
1.5.DefaultSingletonBeanRegistry getSingleton(beanName, ObjectFactory)
1.6.DefaultSingletonBeanRegistry beforeSingletonCreation() -> this.singletonsCurrentlyInCreation.add(beanName) singletonsCurrentlyInCreation[A]
    2.0.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory createBean() -> doCreateBean() -> createBeanInstance() A实例初始化完成
    2.1.boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName)) 返回true
    2.2.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, A实例)); 如果A实例有SmartInstantiationAwareBeanPostProcessor处理器，延时调用生成动态代理对象 (singletonObjects 空，earlySingletonObjects空，singletonFactories[ObjectFactory(A)])
    2.3.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory populateBean() -> applyPropertyValues()
    2.4.BeanDefinitionValueResolver resolveValueIfNecessary(B) -> resolveInnerBean(B)
    2.5.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory getBean(B)

A实例已创建，往A中注入B，初始化B实例
    2.6.AbstractBeanFactory getBean(B) -> doGetBean(B) -> getSingleton(B)
    2.7.DefaultSingletonBeanRegistry getSingleton(B) 返回null
    2.8.DefaultSingletonBeanRegistry getSingleton(B, ObjectFactory)
    2.9.DefaultSingletonBeanRegistry beforeSingletonCreation() -> this.singletonsCurrentlyInCreation.add(B) singletonsCurrentlyInCreation[B]
        3.0.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory createBean() -> doCreateBean() -> createBeanInstance() B实例初始化完成
        3.1.boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName)) 返回true
        3.2.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, B实例)); 如果A实例有SmartInstantiationAwareBeanPostProcessor处理器，延时调用生成动态代理对象 (singletonObjects 空，earlySingletonObjects空，singletonFactories[ObjectFactory(A), ObjectFactory(B)])
        3.3.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory populateBean() -> applyPropertyValues()
        3.4.BeanDefinitionValueResolver resolveValueIfNecessary(B) -> resolveInnerBean(A)
        3.5.DefaultListableBeanFactory AbstractAutowireCapableBeanFactory getBean(A)

A实例已创建，往A中注入B，B实例已创建,往B中注入A
        3.6.AbstractBeanFactory getBean(A) -> doGetBean(A) -> getSingleton(A)
        3.7.DefaultSingletonBeanRegistry getSingleton(A) 调用getEarlyBeanReference提前获取A的引用 (singletonObjects 空，earlySingletonObjects[A]，singletonFactories[ObjectFactory(B)])
        3.8.DefaultSingletonBeanRegistry addSingleton(A) (singletonObjects[A]，earlySingletonObjects 空，singletonFactories[ObjectFactory(B)])
A实例已创建，往A中注入B，B实例已创建,往B中注入A完成
    2.10.DefaultSingletonBeanRegistry addSingleton(B, ObjectFactory) (singletonObjects[A,B]，earlySingletonObjects 空，singletonFactories 空)
1.7.DefaultSingletonBeanRegistry addSingleton(A, ObjectFactory) (singletonObjects[A,B]，earlySingletonObjects 空，singletonFactories 空)


框架核心类：
Mybatis: 
Configuration：所有配置都在这里
SqlSessionFactory: 生产一个session
SqlSessionTemplate: 发送sql查询请求
@Mapper:定义在mapper类
MapperProxy:mapper类的动态代理类，JDK动态代理

sharding-jdbc
读写分离业务
1.application.yml配置文件：spring.shardingsphere.masterslave:
2.springboot自动配置，注入MasterSlaveDataSource
3.SpringTransactionManager承接mybatis和DataSource桥梁，DataSource新增复用池，jdbc没有复用池
MasterSlaveDataSource 中包含多数据源DataSource，包装三方c3p0等连接池
4.发动sql，对sql内容解析，select默认走从库，其他走master库，HintManager中setMasterRouteOnly()方法强制走主库
    实现类：MasterSlaveDataSourceRouter


缓存业务总结
普通读多写少业务
1.写：采用先更新数据库 -> 删除缓存方式
2.读（Read Through）：先缓存->有就返回缓存，没有就从数据库加载数据，保存到缓存，再返回

高并发读多写少
1.写：采用先更新数据库 -> 删除缓存方式
    （1）数据可能不一致，假如某个用户数据在缓存中不存在，请求 A 读取数据时从数据库中查询到年龄为 20，在未写入缓存中时另一个请求 B 更新数据。它更新数据库中的年龄为 21，并且清空缓存。这时请求 A 把从数据库中读到的年龄为 20 的数据写入到缓存中，造成缓存和数据库数据不一致。 出现概率小，一般读比更新快。
        解决方案：最终一致性方案：延时双删，延时间隔为读业务的响应时间
     
2.缓存击穿：删除缓存或者缓存过期后读流量大，造成数据库压力大，甚至挂掉。解决方案：只有一个线程访问数据库（分布式锁），其余线程简单自旋，等缓存加载完成后，其他线程从缓存读取数据，如果缓存加载慢，就快速失败，如果不想失败，就使用servlet3.0异步写response，快速释放tomcat资源

也可采用以下方案
1.写（Write Through）：先更新数据库 -> 更新缓存，需加分布式锁
2.读（Read Through）先缓存->有就返回缓存，没有就从数据库加载数据，保存到缓存，再返回

高并发读写
1.写（Write Through）：(1)不能直接写库，发送写操作到mq，异步消费写入，(2)先更新数据库 -> 更新缓存，需加分布式锁
2.读（Read Through）
(1)先缓存->有就返回缓存，没有就从数据库加载数据，保存到缓存，再返回
(2)缓存预热，即定时任务主动加载数据到缓存, 达到缓存永不过期的效果


操作缓存或者数据库失败
1.操作缓存失败：（1）发送mq重试 （2）.本地事务表（WAL）定时任务重试
2.数据库失败：回滚

缓存穿透，极热点数据
1.穿透的线程、开启分布式锁，没拿到锁的快速返回
2.定时任务监控key的过期时间，临近过期重新构建缓存，主动预热缓存，

数据迁移（db, redis）
1.准备新库
2.新库作为从库关联旧库，等数据赶上后，并保持数据持续跟进，如果牵扯到增加分库分表（分片）逻辑，只能采用中间件（camel），进行下一步
3.修改代码 增加双写，新库采用异步写入
4.增加写新库开关，写旧库开关，默认写新库关，写旧库开，增加读新库/旧库开关，默认读新库关，读旧库开
4.新增数据自定义校验代码
5.发版上线后，先打开双写开关，看到新库写入报错后，断开db同步映射，观察新库写入是否正常
6.验证数据完整性，无问题，切读流量到新库
7.无问题，切写流量到新库

spring bean生命周期
1.回调InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation()    
2.创建bean实例
3.回调InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation()
4.bean填充属性
5.回调BeanPostProcessor.postProcessBeforeInitialization()
6.bean初始化方法
7.回调BeanPostProcessor.postProcessAfterInitialization()

spring aop代理对象生成时机
@EnableAutoProxy 注册了一个AnnotationAwareAspectJAutoProxyCreator 类型InstantiationAwareBeanPostProcessor进bean工厂

1.提前暴露引用时，获取bean的方法延时到-> getEarlyReference, 回调SmartInstantiationAwareBeanPostProcessor.getEarlyBeanReference() bean遍历所有的advisor，如果有匹配的切点就创建代理对象
2.BeanPostProcessor.postProcessAfterInitialization()
@Validated放在类上依赖循环报错原因，MethodValidationPostProcessor属于第二类方式代理，bean提前暴露的引用和最终引用不一致导致
解决方式：1.@Lazy 2.使用第一种方式提前暴露

@Bean("validatedAdvisor")
public DefaultPointcutAdvisor validatedAdvisor(){
    return new DefaultPointcutAdvisor(new StaticMethodMatcherPointcut() {
    public boolean matches(Method method, Class<?> targetClass) {
        return method.isAnnotationPresent(Validated.class) || targetClass.isAnnotationPresent(Validated.class);
    }
    }, new MethodValidationInterceptor());
}


生产配置：
http:
db:
机器内存配置和连接数对应关系	
4c8G	400
8c16G	800
8c24G	1200
应用：需要保证连接数的使用率不要超过80%，必须有连接池
Mysql会话超时时长最大不能超过600秒(10分钟)
最大连接数应尽量小,节省资源。计算公式为: maxActive * 应用的实例 <  DB数据库最大连接数。注意：当不同应用共同使用一个的DB的时候，所有应用的maxActive * 实例个数 < DB数据库最大连接数。
#最大连接数，推荐值：20-100
maxActive=100
#最大空闲连接数，高并发场景可以设置为同maxActive
maxIdle=50
#最小空闲连接数
minIdle=10
#当连接池中连接已经用完了，等待建立一个新连接的最大毫秒数 ( 在抛异常之前 )
maxWait=2000

redis:
kafka:
1.所有消息存储在磁盘上，会有3个备份，确保宕机不丢失数据。允许消费者从某个时间点开始，对原有的消息队列进行消息重放
2.生产者（单队列约500W+ /每小时），消费者（单队列约300W+ /每小时），单个MQ最好不要超过50K ，MQ消息体越小，吞吐量越高。单个MQ体积超过1M时，系统将直接丢弃并报错。Consumer
最佳实践：
Consumer
优化每条消息的处理过程，提交消费性能。
尽量使用批量方式并启用多个线程实例消费数据，可以很大程度提高吞吐量。
消费过程要做到幂等，方便将来对消息进行重放。
Producer
对于消息不可丢失应用，务必要有消息重发机制。例如：消息发送失败，存储到数据库，有定时任务或人工手动触发重发数。
顺序消息，要设置msgKey,相同msgkey的消息会被分配到一个队列中被消费者消费。
每个消息在业务层面的唯一标识码，要设置到id字段，方便将来定位消息丢失问题。消息发送成功或失败，要打印消息日志，务必打印id，msgKey。


秒杀业务：
库存数据预热放到redis中
库存数据可以膨胀1.2（系数自定义），主要redis限制请求进入，最终数据库保证最终秒杀结果
