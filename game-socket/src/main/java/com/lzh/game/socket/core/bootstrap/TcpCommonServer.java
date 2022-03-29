package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.config.GameServerSocketProperties;
import com.lzh.game.socket.core.coder.ExchangeProtocol;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpCommonServer extends AbstractServerBootstrap
        implements GameServer {

    public TcpCommonServer(GameServerSocketProperties properties, SessionManage<Session> sessionManage) {
        super(properties, sessionManage);
    }

    @Override
    protected NetServer createServer(int port) {
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(workerGroup)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childHandler(channelHandler());
        NetServer server = new NetServer(port);
        server.setBootstrap(bootstrap);
        server.setEventLoopGroup(workerGroup);
        return server;
    }

    private ChannelHandler channelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LoggingHandler(properties.getNettyLogLevel()))
                        .addLast(new IdleStateHandler(0, 0, 180, TimeUnit.SECONDS))
                        .addLast(new ProtobufVarint32FrameDecoder())
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        .addLast(new ProtobufEncoder())
                        .addLast(new ProtobufDecoder(ExchangeProtocol.Request.getDefaultInstance()))
                        .addLast(getServerIoHandler())
                ;
            }
        };
    }
}
