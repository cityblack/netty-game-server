package com.lzh.game.socket.exchange.handle;

import com.lzh.game.socket.dispatcher.DispatcherHandler;
import com.lzh.game.socket.exchange.coder.ExchangeProtocol;
import com.lzh.game.socket.exchange.session.ChannelSessionManage;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

public class CommonChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private DispatcherHandler dispatcher;

    @Autowired
    private ChannelSessionManage sessionManage;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new ProtobufDecoder(ExchangeProtocol.Request.getDefaultInstance()))
                .addLast(new GameHandler(dispatcher,sessionManage))
                .addLast(new IdleStateHandler(0,0,180,TimeUnit.SECONDS));
    }
}
