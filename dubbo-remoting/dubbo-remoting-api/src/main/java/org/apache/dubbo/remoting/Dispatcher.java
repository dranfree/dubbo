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
package org.apache.dubbo.remoting;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.remoting.transport.dispatcher.all.AllDispatcher;

/**
 * 线程派发器：创建具有线程派发能力的 {@link ChannelHandler}，只是个工厂。
 * 默认所有消息都派发到线程池，包括请求、响应、连接事件、断开事件等。
 * <p>
 * ChannelHandlerWrapper (SPI, Singleton, ThreadSafe)
 *
 * @see AllDispatcher 缺省配置，所有消息都派发到线程池中执行。
 * @see org.apache.dubbo.remoting.transport.dispatcher.direct.DirectDispatcher 所有消息都不派发到线程池，全部在IO线程上执行。
 * @see org.apache.dubbo.remoting.transport.dispatcher.message.MessageOnlyDispatcher 只有请求和响应消息派发到线程池
 * @see org.apache.dubbo.remoting.transport.dispatcher.execution.ExecutionDispatcher 只有请求消息派发到线程池
 * @see org.apache.dubbo.remoting.transport.dispatcher.connection.ConnectionOrderedDispatcher
 */
@SPI(AllDispatcher.NAME)
public interface Dispatcher {

    /**
     * dispatch the message to threadpool.
     *
     * @param handler
     * @param url
     * @return channel handler
     */
    @Adaptive({Constants.DISPATCHER_KEY, "dispather", "channel.handler"})
    // The last two parameters are reserved for compatibility with the old configuration
    ChannelHandler dispatch(ChannelHandler handler, URL url);

}
