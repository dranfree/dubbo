/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.dubbo.config.annotation;

import org.apache.dubbo.config.ReferenceConfigBase;

import java.lang.annotation.*;

@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Reference {

    /**
     * 接口类型
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口名称，比 {@link this#interfaceClass()} 优先解析
     */
    String interfaceName() default "";

    /**
     * 定义dubbo服务版本，用于隔离同一个服务的多个版本。
     */
    String version() default "";

    /**
     * dubbo在寻找提供者的时候，需要服务接口、版本、分组三个完全一致，可以配置为“*”，表示查询所有分组。
     */
    String group() default "";

    /**
     * 直连提供者，配置类似：dubbo://10.20.153.10:20880
     * <p>
     * 直连提供者的其他配置方式：
     * <ol>
     *     <li>使用JVM参数配置：-Dcom.xxx.XxxService=dubbo://10.20.153.10:20880</li>
     *     <li>使用映射文件配置：${user.home}/dubbo-resolve.properties -> com.xxx.XxxService=dubbo://10.20.153.10:20880</li>
     * </ol>
     *
     * @return 服务提供者的直接访问地址
     * @see ReferenceConfigBase#resolveFile() 解析dubbo-resolve.properties配置
     * @see {@code org.apache.dubbo.config.ReferenceConfig#createProxy()} 解析注解的url参数
     */
    String url() default "";

    /**
     * 配置底层通信客户端类型，netty/netty4/mina...，否则取默认配置。
     *
     * @see {@code org.apache.dubbo.remoting.Transporter}
     */
    String client() default "";

    /**
     * 泛化调用，此项为true的时候必须配置{@link this#interfaceName()}属性。
     * <ol>
     *     <li>泛化调用得到的类型为 {@link org.apache.dubbo.rpc.service.GenericService}</li>
     *     <li>有接口类的时候也可以使用泛化调用，不过没什么必要。</li>
     * </ol>
     */
    boolean generic() default false;

    /**
     * 每个服务默认都会在本地暴露，服务引用的时候默认引用本地服务。
     */
    boolean injvm() default true;

    /**
     * 默认启动时检查提供者状态，当找不到提供者的时候直接报错。
     */
    boolean check() default true;

    /**
     * TODO
     *
     * @return
     */
    boolean init() default false;

    /**
     * 延迟连接用于减少长连接数。当有调用发起时，再创建长连接。
     *
     * @see {@code org.apache.dubbo.rpc.protocol.dubbo.LazyConnectExchangeClient}
     */
    boolean lazy() default false;

    boolean stubevent() default false;

    String reconnect() default "";

    /**
     * 粘滞连接，即让每次调用尽可能用同一个服务提供者，除非提供者挂了。
     *
     * @see {@code org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker#select}
     */
    boolean sticky() default false;

    /**
     * 指定创建代理使用的 ProxyFactory 扩展点名称，jdk/javassist
     */
    String proxy() default "";

    /**
     * 本地存根，允许客户端在本地执行部分逻辑，类似装饰器。这种逻辑可以写在业务代码里面，感觉用处不大。
     * <ol>
     *     <li>true：com.xxx.XxxServiceStub</li>
     *     <li>本地Stub实现类的全限定名</li>
     * </ol>
     *
     * @see {@code org.apache.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper}
     */
    String stub() default "";

    /**
     * 配置集群容错策略
     *
     * @see {@code FailbackCluster}
     * @see {@code FailoverCluster}
     * @see {@code FailfastCluster}
     * @see {@code FailsafeCluster}
     * @see {@code ForkingCluster}
     * @see {@code MergeableCluster}
     * @see {@code BroadcastCluster}
     */
    String cluster() default "";

    /**
     * TODO 共享连接数
     *
     * @return
     */
    int connections() default 0;

    /**
     * TODO
     *
     * @return
     */
    int callbacks() default 0;

    /**
     * TODO
     *
     * @return
     */
    String onconnect() default "";

    /**
     * TODO
     *
     * @return
     */
    String ondisconnect() default "";

    /**
     * TODO
     *
     * @return
     */
    String owner() default "";

    /**
     * TODO
     *
     * @return
     */
    String layer() default "";

    /**
     * 重试次数，也可以通过RpcContext进行运行时动态配置，优先级高于注解/xml进行的固定配置。
     * <p>
     * 只对 failback/failover 两种容错策略生效？
     */
    int retries() default 2;

    /**
     * 指定负载均衡策略
     */
    String loadbalance() default "";

    /**
     * 是否异步调用，如果设置为true，那么每次调用都会直接返回，不等待响应，消费端可能会拿到一个null值。
     *
     * @see {@code AsyncToSyncInvoker}
     */
    boolean async() default false;

    /**
     * 限制并发调用数
     *
     * @see {@code ActiveLimitFilter}
     */
    int actives() default 0;

    /**
     * <ol>
     *     <li>true: 等待消息发出，消息发送失败将抛出异常</li>
     *     <li>false: 不等待消息发出，将消息放入 IO 队列，即刻返回</li>
     * </ol>
     *
     * @see {@code NettyChannel}
     */
    boolean sent() default false;

    /**
     * 本地伪装/服务降级，其实这种逻辑也可以写在业务代码里面，感觉用处不大。
     *
     * @see {@code org.apache.dubbo.rpc.cluster.support.wrapper.MockClusterInvoker}
     */
    String mock() default "";

    /**
     * 消费端参数校验，一般配置true就可以，默认使用 validation-api 实现。
     *
     * @return 为true的时候取默认validation
     * @see org.apache.dubbo.common.utils.ConfigUtils#isEmpty 此方法返回false的时候自动激活
     */
    String validation() default "";

    /**
     * 超时配置
     *
     * @see {@code org.apache.dubbo.rpc.protocol.dubbo.DubboInvoker#doInvoke}
     */
    int timeout() default 0;

    /**
     * 激活缓存扩展点
     *
     * @see {@code CacheFilter}
     * @see {@code ProtocolFilterWrapper}
     */
    String cache() default "";

    /**
     * 指定需要激活的Filter扩展点名称，或者可以通过URL参数自动激活。
     *
     * @see {@code ProtocolFilterWrapper}
     */
    String[] filter() default {};

    /**
     * 配置需要激活的远程调用监听器扩展
     *
     * @see {@code InvokerListener}
     * @see {@code ListenerInvokerWrapper}
     * @see {@code ProtocolListenerWrapper}
     */
    String[] listener() default {};

    /**
     * TODO
     *
     * @return
     */
    String[] parameters() default {};

    /**
     * todo
     *
     * @return
     */
    String application() default "";

    /**
     * todo
     *
     * @return
     */
    String module() default "";

    /**
     * todo
     *
     * @return
     */
    String consumer() default "";

    /**
     * todo
     *
     * @return
     */
    String monitor() default "";

    /**
     * TODO 多注册中心的时候指定注册中心id，待测试
     */
    String[] registry() default {};

}

