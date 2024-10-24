package com.lzh.game.framework.socket.core.bootstrap.tcp;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.NetServer;
import com.lzh.game.framework.socket.core.bootstrap.handler.ServerIdleHandler;
import com.lzh.game.framework.socket.core.bootstrap.server.AbstractServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.codec.ByteToGameMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteEncoder;
import com.lzh.game.framework.socket.utils.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpServer<T extends GameServerSocketProperties> extends AbstractServerBootstrap<T>
        implements GameServer {

    public TcpServer(BootstrapContext<T> context) {
        super(context);
    }

    @Override
    protected NetServer createServer(int port, T properties, ProcessorPipeline pipeline) {
        var netty = properties.getNetty();
        final EventLoopGroup bossGroup = NettyUtils.group(netty.isUseEpoll(), netty.getBossWordCore());
        final EventLoopGroup workerGroup = NettyUtils.group(netty.isUseEpoll(), netty.getWorkCore());
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyUtils.serverChannelType(netty.isUseEpoll()))
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childHandler(channelHandler());
        NettyUtils.serverBootAddOptions(bootstrap, properties.getNetty());
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
                        .addLast(new LoggingHandler(getProperties().getNetty().getLogLevel()))
                        .addLast(new IdleStateHandler(getProperties().getServerIdleTime(), 0, 0, TimeUnit.MILLISECONDS))
                        .addLast("serverIdleHandler", new ServerIdleHandler())
                        .addLast("decoder", new ByteToGameMessageDecoder(context))
                        .addLast("encoder", new GameMessageToByteEncoder(context))
                        .addLast(getIoHandler())
                ;
            }
        };
    }
}
