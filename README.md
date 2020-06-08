1、spring aop 实现方法入参以及耗时日志
2、全局处理spring mvc框架异常、spring security异常、自定义异常、spring validation异常
3、jwt filter实现token校验以及spring security认证
4、全局响应体转换
5、方法级别的入参校验，以及方法级别的权限校验
6、jackson java中的null到json中的空转换
7、mybatais generator插件配置，自动生成Serializable、toString、覆盖mapper.xml文件、Equals和HashCode生成、Model流式赋值方法、SelectOneByExample、ModelColumn关联、批量插入
8、mybatis-spring 一级缓存会失效，加上@transactional注解，一级缓存会生效，没有事物多次select会重新创建多次session。实际生产应该禁用第一第二级缓存，采用redis缓存
9、redis实现数据缓存: 集成spring cache，@cacheable key生成策略 userInfo::SimpleKey [arg1,arg2]
10、redis实现api限流：使用令牌桶、和lua脚本实现限流。
    问题1：大量读请求涌入redis，导致redis压力大
    解决1：采用内存限流外加+redis分布式限流方案，降低redis并发量
11、redis分布式锁：使用redisson实现分布式锁，RedLock向不止一个redis master发送请求，超过半数以上算数
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
future:
zookeeper
dubbo
message queue
