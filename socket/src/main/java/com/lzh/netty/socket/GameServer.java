package com.lzh.netty.socket;

import com.lzh.netty.socket.dispatcher.DispatcherHandler;
import com.lzh.netty.socket.protocol.coder.AbstractRequest;
import com.lzh.netty.socket.protocol.handle.GameHandler;
import com.lzh.netty.socket.protocol.session.ChannelSession;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;


@Slf4j
public class GameServer implements InitializingBean {

    private ServerBootstrap bootstrap;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final DispatcherHandler dispatcher;
    private final int port;
    private final ChannelSession sessionManage;

    public GameServer(DispatcherHandler dispatcher, ChannelSession sessionManage, int port) {
        this.dispatcher = dispatcher;
        this.port = port;
        this.sessionManage = sessionManage;
    }

    private void start(int port) {

        try {
            ChannelFuture f = this.bootstrap.bind(port).sync();
            new Thread(() -> {
                try {
                    log.info("Netty started on {}", port);
                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    log.error("Netty start error 0.0, {}",e);
                    close();
                }
            }).start();

        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("netty start error 0.0, {}",e);
            close();
        }
    }

    protected void init() {
        this.bootstrap = new ServerBootstrap();
        bootstrap.group(workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(new ProtobufDecoder(AbstractRequest.Request.getDefaultInstance()))

                                //.addLast(new GameDecoder())
                                .addLast(new GameHandler(dispatcher,sessionManage));

                    }
                });
    }

    public void close() {
        workerGroup.shutdownGracefully();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        start();
    }

    public void start() {
        this.start(this.port);
    }
}
