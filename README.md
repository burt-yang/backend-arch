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


三级缓存流程

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

