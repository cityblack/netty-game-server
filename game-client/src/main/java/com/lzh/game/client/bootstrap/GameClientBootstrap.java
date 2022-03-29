package com.lzh.game.client.bootstrap;

import com.lzh.game.client.support.ExchangeProtocol;
import com.lzh.game.common.scoket.AbstractBootstrap;
import com.lzh.game.common.scoket.GameIoHandler;
import com.lzh.game.common.scoket.GameSocketProperties;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class GameClientBootstrap extends AbstractBootstrap<GameSocketProperties>
        implements TcpClient {

    private EventLoopGroup group = new NioEventLoopGroup();

    private MessageHandler handler;

    private SessionManage<ClientGameSession> sessionManage;

    public GameClientBootstrap(MessageHandler handler, SessionManage<ClientGameSession> sessionManage, GameSocketProperties properties) {
        super(properties);
        this.handler = handler;
        this.sessionManage = sessionManage;
    }

    @Override
    public Session conn(String host, int port) {
        Bootstrap bootstrap = createBootstrap();
        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();
            return sessionManage.getSession(channel);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SessionManage<ClientGameSession> sessionManage() {
        return sessionManage;
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

    private GameIoHandler<ClientGameSession> clientHandler() {
        return new GameIoHandler<>(handler, sessionManage);
    }

    @Override
    public MessageHandler messageHandler() {
        return null;
    }

    @Override
    protected void init(ApplicationContext context) {

    }

    @Override
    protected void doStart() {

    }
}
