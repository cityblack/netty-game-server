package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.coder.GameByteToMessageDecoder;
import com.lzh.game.common.scoket.coder.GameMessageToMessageDecoder;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.GameServerSocketProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpCommonServer extends AbstractServerBootstrap
        implements GameServer {

    public TcpCommonServer(GameServerSocketProperties properties, SessionManage<Session> sessionManage, MessageHandler handler) {
        super(properties, sessionManage, handler);
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
//                        .addLast(new LoggingHandler(properties.getNettyLogLevel()))
                        .addLast(new IdleStateHandler(0, 0, 180, TimeUnit.SECONDS))
                        .addLast("encoder", new GameByteToMessageDecoder())
                        .addLast("decoder",new GameMessageToMessageDecoder())
                        .addLast(getServerIoHandler())
                ;
            }
        };
    }
}
