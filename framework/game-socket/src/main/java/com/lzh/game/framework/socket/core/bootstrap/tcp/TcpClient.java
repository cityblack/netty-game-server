package com.lzh.game.framework.socket.core.bootstrap.tcp;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AbstractClient;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClient;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.handler.ClientIdleHandler;
import com.lzh.game.framework.socket.core.protocol.codec.ByteToGameMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Client
 * @param <C>
 */
@Slf4j
public class TcpClient<C extends GameClientSocketProperties> extends AbstractClient<C>
        implements GameClient {

    public TcpClient(BootstrapContext<C> context) {
        super(context);
    }

    protected Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(getProperties().getNetty().getLogLevel()))
                                .addFirst(new IdleStateHandler(0, getProperties().getClientIdleTime(), 0, TimeUnit.MILLISECONDS))
                                .addFirst(new ClientIdleHandler())
                                .addLast("decoder", new ByteToGameMessageDecoder(context))
                                .addLast("encoder", new GameMessageToByteEncoder(context))
                                .addLast(getIoHandler());
                    }
                });
        return bootstrap;
    }

    @Override
    protected void doInit(C properties) {
        if (Objects.isNull(getRequestService())) {
            setRequestService(Executors.newCachedThreadPool());
        }
    }
}
