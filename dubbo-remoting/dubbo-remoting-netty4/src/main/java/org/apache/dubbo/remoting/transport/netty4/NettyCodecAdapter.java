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
package org.apache.dubbo.remoting.transport.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.Codec2;
import org.apache.dubbo.remoting.buffer.ChannelBuffer;

import java.io.IOException;
import java.util.List;

/**
 * NettyCodecAdapter.
 */
final public class NettyCodecAdapter {

    private final ChannelHandler encoder = new InternalEncoder();

    private final ChannelHandler decoder = new InternalDecoder();

    private final Codec2 codec;

    private final URL url;

    private final org.apache.dubbo.remoting.ChannelHandler handler;

    public NettyCodecAdapter(Codec2 codec, URL url, org.apache.dubbo.remoting.ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
    }

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    private class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            org.apache.dubbo.remoting.buffer.ChannelBuffer buffer = new NettyBackedChannelBuffer(out);
            Channel ch = ctx.channel();
            NettyChannel channel = NettyChannel.getOrAddChannel(ch, url, handler);
            codec.encode(channel, buffer, msg);
        }
    }

    private class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {

            // 只是一层装饰，兼容不同的Codec实现。
            // 封装netty的ByteBuf为dubbo内部统一的buffer模型
            ChannelBuffer message = new NettyBackedChannelBuffer(input);

            NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);

            // decode object.
            // 解析结果：
            // 1.至少0条完整的dubbo协议栈数据
            // 2.至多1条不完整的dubbo协议栈数据
            // 所以这里的 out 参数是个 List 对象，并且使用一个循环去处理。
            // 一次循环没读完，说明是粘包。
            do {
                // 保存当前读到的buffer位置，用于拆包时消息回滚。
                int saveReaderIndex = message.readerIndex();
                // 这里返回的结果可能是：
                // 1.Request/Response
                // 2.Codec2.DecodeResult.NEED_MORE_INPUT，即拆包。
                Object msg = codec.decode(channel, message);
                if (msg == Codec2.DecodeResult.NEED_MORE_INPUT) {
                    // 缓存不完整的dubbo协议栈数据，等待下次inbound事件，合并进去。
                    // 这里是不完整的协议栈数据，肯定是解析到数据包末尾了，直接跳出循环即可。
                    // 在下一个inbound事件到来时，Netty会将下一个数据包和这个剩余的数据包合并。
                    message.readerIndex(saveReaderIndex);
                    break;
                } else {
                    //is it possible to go here ?
                    //decode方法没有解析任何数据，可能吗？
                    if (saveReaderIndex == message.readerIndex()) {
                        throw new IOException("Decode without read data.");
                    }
                    if (msg != null) {
                        out.add(msg); // 完整解析出来的消息对象(Request/Response)，添加进响应列表中。
                    }
                }
            } while (message.readable());
        }
    }
}
