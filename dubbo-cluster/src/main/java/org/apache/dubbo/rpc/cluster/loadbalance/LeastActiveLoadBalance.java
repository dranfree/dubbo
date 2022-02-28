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
package org.apache.dubbo.rpc.cluster.loadbalance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcStatus;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加权最小活跃调用数负载均衡
 * <p>
 * 活跃调用数越小，表明该服务提供者效率越高，单位时间内可处理更多的请求。此时应优先将请求分配给该服务提供者。
 * <p>
 * 基本实现思想：每收到一个请求，活跃数加1，完成请求后则将活跃数减1。
 * <p>
 * Filter the number of invokers with the least number of active calls and count the weights and quantities of these invokers.
 * If there is only one invoker, use the invoker directly;
 * if there are multiple invokers and the weights are not the same, then random according to the total weight;
 * if there are multiple invokers and the same weight, then randomly called.
 */
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "leastactive";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // Number of invokers
        int length = invokers.size();
        // 最小活跃数
        // The least active value of all invokers
        int leastActive = -1;
        // 具有“最小活跃数”的服务提供者数量
        // The number of invokers having the same least active value (leastActive)
        int leastCount = 0;
        // 记录 “具有最小活跃调用数” 的 Invoker 在列表中的位置，最终的 Invoker 从这里面产生。
        // The index of invokers having the same least active value (leastActive)
        int[] leastIndexes = new int[length];
        // the weight of every invokers
        // 记录每个 Invoker 的权重
        int[] weights = new int[length];
        // 所有 Invoker 的总权重
        // The sum of the warmup weights of all the least active invokers
        int totalWeight = 0;
        // 第一个 “最小活跃调用数” 的 Invoker 的权重
        // The weight of the first least active invoker
        int firstWeight = 0;
        // 是否每个 “least active invoker” 都有相同的权重
        // Every least active invoker has the same weight value?
        boolean sameWeight = true;

        // 遍历 Invoker 列表
        // Filter out all the least active invokers
        for (int i = 0; i < length; i++) {
            Invoker<T> invoker = invokers.get(i);
            // 当前 Invoker 的活跃调用数
            // Get the active number of the invoker
            int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive();
            // Invoker 的权重
            // Get the weight of the invoker's configuration. The default value is 100.
            int afterWarmup = getWeight(invoker, invocation);
            // save for later use
            weights[i] = afterWarmup;
            // If it is the first invoker or the active number of the invoker is less than the current least active number
            if (leastActive == -1 || active < leastActive) {
                // 重设最小活跃调用数
                // Reset the active number of the current invoker to the least active number
                leastActive = active;
                // 重置最小活跃调用数提供者数量
                // Reset the number of least active invokers
                leastCount = 1;
                // 第一个 “least active invoker” 的 Invoker 的位置
                // Put the first least active invoker first in leastIndexes
                leastIndexes[0] = i;
                // 重置总权重
                // Reset totalWeight
                totalWeight = afterWarmup;
                // 第一个 “least active invoker” 的权重
                // Record the weight the first least active invoker
                firstWeight = afterWarmup;
                // Each invoke has the same weight (only one invoker here)
                sameWeight = true;
                // If current invoker's active value equals with leaseActive, then accumulating.
            } else if (active == leastActive) {
                // 记录 Invoker 位置
                // Record the index of the least active invoker in leastIndexes order
                leastIndexes[leastCount++] = i;
                // 总权重 +
                // Accumulate the total weight of the least active invoker
                totalWeight += afterWarmup;
                // If every invoker has the same weight?
                if (sameWeight && i > 0
                        && afterWarmup != firstWeight) {
                    // 权重不相同
                    sameWeight = false;
                }
            }
        }
        // Choose an invoker from all the least active invokers
        if (leastCount == 1) {
            // If we got exactly one invoker having the least active value, return this invoker directly.
            return invokers.get(leastIndexes[0]);
        }
        // 从 “具有最小活跃调用数” 的 Invoker 中根据权重加权后选择一个出来。
        if (!sameWeight && totalWeight > 0) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on 
            // totalWeight.
            // 随机生成一个 [0, totalWeight) 之间的数字
            int offsetWeight = ThreadLocalRandom.current().nextInt(totalWeight);
            // Return a invoker based on the random value.
            // 循环让随机数减去具有最小活跃数的 Invker 的权重值
            // 当 offset 小于等于 0 时，返回相应的 Invoker
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexes[i];
                // 让随机数减去权重值 ★
                offsetWeight -= weights[leastIndex];
                if (offsetWeight < 0) {
                    return invokers.get(leastIndex);
                }
            }
        }
        // 所有 “具有最小活跃调用数” 的 Invoker 具有相同的权重，随即返回一个即可。
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(leastIndexes[ThreadLocalRandom.current().nextInt(leastCount)]);
    }
}
