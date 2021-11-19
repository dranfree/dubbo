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
package org.apache.dubbo.config.annotation;


import java.lang.annotation.*;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_LOADBALANCE;
import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_RETRIES;

/**
 * Service annotation
 *
 * @export
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Service {

    /**
     * Interface class, default value is void.class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Interface class name, default value is empty string
     */
    String interfaceName() default "";

    /**
     * Service version, default value is empty string
     *
     * @see Reference#version()
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     *
     * @see Reference#group()
     */
    String group() default "";

    /**
     * Service path, default value is empty string
     */
    String path() default "";

    /**
     * Whether to export service, default value is true
     */
    boolean export() default true;

    /**
     * Service token, default value is false
     */
    String token() default "";

    /**
     * Whether the service is deprecated, default value is false，只是打点日志没有实际业务作用。
     *
     * @see {@code DeprecatedFilter}
     * @see {@code DeprecatedInvokerListener}
     */
    boolean deprecated() default false;

    /**
     * 人工上下线
     * <p>
     * Whether the service is dynamic, default value is true
     */
    boolean dynamic() default true;

    /**
     * Access log for the service, default value is ""
     */
    String accesslog() default "";

    /**
     * 和 {@link this#actives()} 有什么区别？
     * <p>
     * Maximum concurrent executes for the service, default value is 0 - no limits
     */
    int executes() default 0;

    /**
     * 是否要将服务注册到注册中心，一般不会改成false。
     * <p>
     * Whether to register the service to register center, default value is true
     */
    boolean register() default true;

    /**
     * 调用权重
     * <p>
     * Service weight value, default value is 0
     */
    int weight() default 0;

    /**
     * 文档？应该没什么实际业务作用，也许和 dubbo-admin 有关系。
     * <p>
     * Service doc, default value is ""
     */
    String document() default "";

    /**
     * 延迟暴露
     * <p>
     * Delay time for service registration, default value is 0
     *
     * @see {@code org.apache.dubbo.config.ServiceConfig#export()}
     */
    int delay() default 0;

    /**
     * @see Service#stub()
     * @deprecated
     */
    String local() default "";

    /**
     * Service stub name, use interface name + Local if not set
     */
    String stub() default "";

    /**
     * Cluster strategy, legal values include: failover, failfast, failsafe, failback, forking
     *
     * @see Reference#cluster()
     */
    String cluster() default "";

    /**
     * How the proxy is generated, legal values include: jdk, javassist
     *
     * @see Reference#proxy()
     */
    String proxy() default "";

    /**
     * Maximum connections service provider can accept, default value is 0 - connection is shared
     */
    int connections() default 0;

    /**
     * The callback instance limit peer connection
     *
     * see org.apache.dubbo.rpc.Constants#DEFAULT_CALLBACK_INSTANCES
     */
    int callbacks() default org.apache.dubbo.common.constants.CommonConstants.DEFAULT_CALLBACK_INSTANCES;

    /**
     * Callback method name when connected, default value is empty string
     */
    String onconnect() default "";

    /**
     * Callback method name when disconnected, default value is empty string
     */
    String ondisconnect() default "";

    /**
     * Service owner, default value is empty string
     */
    String owner() default "";

    /**
     * Service layer, default value is empty string
     */
    String layer() default "";

    /**
     * Service invocation retry times
     *
     * @see org.apache.dubbo.common.constants.CommonConstants#DEFAULT_RETRIES
     * @see Reference#retries()
     */
    int retries() default DEFAULT_RETRIES;

    /**
     * Load balance strategy, legal values include: random, roundrobin, leastactive
     *
     * @see org.apache.dubbo.common.constants.CommonConstants#DEFAULT_LOADBALANCE
     * @see Reference#loadbalance()
     */
    String loadbalance() default DEFAULT_LOADBALANCE;

    /**
     * Whether to enable async invocation, default value is false
     *
     * @see Reference#async()
     */
    boolean async() default false;

    /**
     * Maximum active requests allowed, default value is 0
     *
     * @see Reference#actives()
     */
    int actives() default 0;

    /**
     * Whether the async request has already been sent, the default value is false
     *
     * @see Reference#sent()
     */
    boolean sent() default false;

    /**
     * Service mock name, use interface name + Mock if not set
     */
    String mock() default "";

    /**
     * Whether to use JSR303 validation, legal values are: true, false
     *
     * @see Reference#validation()
     */
    String validation() default "";

    /**
     * Timeout value for service invocation, default value is 0
     *
     * @see Reference#timeout()
     */
    int timeout() default 0;

    /**
     * Specify cache implementation for service invocation, legal values include: lru, threadlocal, jcache
     * @see Reference#cache()
     */
    String cache() default "";

    /**
     * Filters for service invocation
     *
     * @see Filter
     * @see {@code ProtocolFilterWrapper}
     * @see Reference#filter()
     */
    String[] filter() default {};

    /**
     * Listeners for service exporting and unexporting
     *
     * @see ExporterListener
     * @see Reference#listener()
     * @see {@code ProtocolListenerWrapper}
     */
    String[] listener() default {};

    /**
     * Customized parameter key-value pair, for example: {key1, value1, key2, value2}
     */
    String[] parameters() default {};

    /**
     * Application spring bean name
     */
    String application() default "";

    /**
     * Module spring bean name
     */
    String module() default "";

    /**
     * Provider spring bean name
     */
    String provider() default "";

    /**
     * Protocol spring bean names
     */
    String[] protocol() default {};

    /**
     * Monitor spring bean name
     */
    String monitor() default "";

    /**
     * Registry spring bean name
     */
    String[] registry() default {};

    /**
     * Service tag name
     */
    String tag() default "";

    /**
     * 单独定义每个method的行为
     *
     * methods support
     * @return
     */
    Method[] methods() default {};
}
