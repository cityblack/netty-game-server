package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.NetServer;
import com.lzh.game.framework.socket.core.bootstrap.ServerIdleHandler;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.codec.GameByteToMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteDecoder;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpCommonServer<T extends GameServerSocketProperties> extends AbstractServerBootstrap<T>
        implements GameServer {

    public TcpCommonServer(T properties, SessionManage<Session> sessionManage, MessageManager messageManager, InvokeSupport invokeSupport) {
        super(properties, sessionManage, messageManager, invokeSupport);
    }

    public TcpCommonServer(T properties, SessionManage<Session> sessionManage) {
        super(properties, sessionManage);
    }

    public TcpCommonServer(T properties) {
        super(properties);
    }

    @Override
    protected NetServer createServer(int port, T properties, ProcessorPipeline pipeline) {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(properties.getBossWordCore());
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childHandler(channelHandler());
        NetServer server = new NetServer(port, properties, pipeline);
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
