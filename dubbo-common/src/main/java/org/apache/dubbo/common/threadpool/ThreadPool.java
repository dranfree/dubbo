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
package org.apache.dubbo.common.threadpool;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

import java.util.concurrent.Executor;

import static org.apache.dubbo.common.constants.CommonConstants.THREADPOOL_KEY;

/**
 * ThreadPool
 *
 * @see org.apache.dubbo.common.threadpool.support.fixed.FixedThreadPool
 * 固定大小线程池，类似 {@link java.util.concurrent.Executors#newFixedThreadPool}
 * @see org.apache.dubbo.common.threadpool.support.cached.CachedThreadPool
 * 缓存线程池，默认空闲一分钟自动删除。
 * @see org.apache.dubbo.common.threadpool.support.limited.LimitedThreadPool
 * 可伸缩线程池，但是池中的线程数只会增长不会收缩，目的是为了避免收缩时突然来了大流量引起性能问题。
 */
@SPI("fixed")
public interface ThreadPool {

    /**
     * Thread pool
     *
     * @param url URL contains thread parameter
     * @return thread pool
     */
    @Adaptive({THREADPOOL_KEY})
    Executor getExecutor(URL url);

}
