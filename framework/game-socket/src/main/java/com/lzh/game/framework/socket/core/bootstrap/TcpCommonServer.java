package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.codec.GameByteToMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteDecoder;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpCommonServer extends AbstractServerBootstrap
        implements GameServer {

    protected TcpCommonServer(GameServerSocketProperties properties, SessionManage<? extends Session> sessionManage, ProcessorPipeline pipeline, MessageManager messageManager) {
        super(properties, sessionManage, pipeline, messageManager);
    }

    protected TcpCommonServer(GameServerSocketProperties properties, SessionManage<? extends Session> sessionManage) {
        super(properties, sessionManage);
    }

    protected TcpCommonServer(GameServerSocketProperties properties) {
        super(properties);
    }

    @Override
    protected NetServer createServer(int port) {
        final EventLoopGroup workerGroup = new EpollEventLoopGroup();
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
                        .addLast(new IdleStateHandler(0, 0, getProperties().getServerIdleTime(), TimeUnit.MILLISECONDS))
                        .addLast("serverIdleHandler", new ServerIdleHandler())
                        .addLast("decoder", new GameByteToMessageDecoder(getMessageManager()))
                        .addLast("encoder", new GameMessageToByteDecoder(getMessageManager()))
                        .addLast(getIoHandler())
                ;
            }
        };
    }
}
