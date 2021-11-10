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
package org.apache.dubbo.remoting.exchange;

import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.RemotingException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * 在Exchange层，将message划分为Request和Response两种类型，并且在这 两个模型上增加一些系统字段来处理问题。
 * Dubbo将一条消息分为了协议头和内容两个部分：协议头包括系统字段，例如编号等，内容包括具体请求的参数和响应的结果等。
 * 在Exchange层，大量逻辑是基于协议头的。
 * ExchangeChannel. (API/SPI, Prototype, ThreadSafe)
 */
public interface ExchangeChannel extends Channel {

    /**
     * send request.
     *
     * @param request
     * @return response future
     * @throws RemotingException
     */
    @Deprecated
    CompletableFuture<Object> request(Object request) throws RemotingException;

    /**
     * send request.
     *
     * @param request
     * @param timeout
     * @return response future
     * @throws RemotingException
     */
    @Deprecated
    CompletableFuture<Object> request(Object request, int timeout) throws RemotingException;

    /**
     * send request.
     *
     * @param request
     * @return response future
     * @throws RemotingException
     */
    CompletableFuture<Object> request(Object request, ExecutorService executor) throws RemotingException;

    /**
     * send request.
     *
     * @param request
     * @param timeout
     * @return response future
     * @throws RemotingException
     */
    CompletableFuture<Object> request(Object request, int timeout, ExecutorService executor) throws RemotingException;

    /**
     * get message handler.
     *
     * @return message handler
     */
    ExchangeHandler getExchangeHandler();

    /**
     * graceful close.
     *
     * @param timeout
     */
    @Override
    void close(int timeout);
}
