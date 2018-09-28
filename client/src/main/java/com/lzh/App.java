package com.lzh;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class App {
    public static String host = "127.0.0.1";
    public static int port = 8099;

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        final HelloClientHandler clientHandler = new HelloClientHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(AbstractResponse.Response.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(clientHandler);
                    }
                });
        Channel ch = bootstrap.connect(host, port).sync().channel();
        testGm(ch);
        testRequest(ch);
    }

    public static void testRequest(Channel ch) {
        Request request = new Request();
        request.setId(10086);
        request.setList(Arrays.asList("l","z","h"));
        request.setValue(new int[]{1,2,3});
        ch.writeAndFlush(AbstractRequest.Request.newBuilder()
                .setProtocolId(10086)
                .setVersion(100101)
                .setData(JSON.toJSONString(request))
                .build());
    }

    public static void testGm(Channel channel) {
        Gm gm = new Gm();
        gm.setMethodName("test");
        gm.setValue("10086");
        AbstractRequest.Request  request = AbstractRequest.Request
                .newBuilder()
                .setProtocolId(1)
                .setVersion(100101)
                .setData(JSON.toJSONString(gm))
                .build();

        channel.writeAndFlush(request);
    }

    @Data
    static class Request {
        private int id;
        private List<String> list;
        private int[] value;
    }

    @Data
    static class Gm {
        private String methodName;
        private String value;
    }
}
