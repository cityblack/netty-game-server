package com.lzh.game.client.bootstrap;

import com.lzh.game.client.dispatcher.ResponseDispatcher;
import com.lzh.game.client.support.ExchangeProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.stereotype.Component;

@Component
public class GameClientBootstrap implements TcpClient {

    private EventLoopGroup group = new NioEventLoopGroup();

    private Channel clientChannel;

    private ResponseDispatcher dispatcher;

    public GameClientBootstrap(ResponseDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void conn(String host, int port) {
        Bootstrap bootstrap = createBootstrap();

        try {
            Channel ch = bootstrap.connect(host, port).sync().channel();
            clientChannel = ch;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(ExchangeProtocol.Response.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(clientHandler());
                    }
                });
        return bootstrap;
    }

    @Override
    public Channel getChannel() {
        return clientChannel;
    }

    private GameClientHandler clientHandler() {
        return new GameClientHandler(dispatcher);
    }
}
