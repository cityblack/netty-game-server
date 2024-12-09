package com.lzh.game.framework.socket.core.bootstrap.tcp;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.NetServer;
import com.lzh.game.framework.socket.core.bootstrap.handler.ServerIdleHandler;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.protocol.codec.ByteToGameMessageDecoder;
import com.lzh.game.framework.socket.core.protocol.codec.GameMessageToByteEncoder;
import com.lzh.game.framework.socket.core.websocket.WebSocketFrameDecoderHandler;
import com.lzh.game.framework.socket.core.websocket.WebSocketFrameEncoderHandler;
import com.lzh.game.framework.socket.utils.NettyUtils;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class TcpServer<T extends ServerSocketProperties> extends ServerBootstrap<T>
        implements GameServer {

    public TcpServer(BootstrapContext<T> context) {
        super(context, null);
        setNetServer(createServer());
    }

    protected NetServer createServer() {
        var netty = getProperties().getNetty();
        final EventLoopGroup bossGroup = NettyUtils.group(netty.isUseEpoll(), netty.getBossWordCore());
        final EventLoopGroup workerGroup = NettyUtils.group(netty.isUseEpoll(), netty.getWorkCore());
        final io.netty.bootstrap.ServerBootstrap bootstrap = new io.netty.bootstrap.ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyUtils.serverChannelType(netty.isUseEpoll()))
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childHandler(channelHandler());
        NettyUtils.serverBootAddOptions(bootstrap, getProperties().getNetty());
        return new NetServer(bootstrap, workerGroup);
    }

    private ChannelHandler channelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                var pipeline = ch.pipeline();
                pipeline
                        .addLast(new LoggingHandler(getProperties().getNetty().getLogLevel()))
                        .addLast(new IdleStateHandler(getProperties().getServerIdleTime(), 0, 0, TimeUnit.MILLISECONDS))
                        .addLast("serverIdleHandler", new ServerIdleHandler());
                setWebSocketFrame(ch, new ByteToGameMessageDecoder(context), new GameMessageToByteEncoder(context));
//                        .addLast("decoder", )
//                        .addLast("encoder", );
//                        .addLast(getIoHandler());
            }
        };
    }

    protected void setWebSocketFrame(Channel ch, ByteToMessageDecoder decoder, MessageToByteEncoder<Object> encoder) {
        var webSocket = getProperties().getWebSocket();
        if (webSocket.isAble()) {
            ch.pipeline().addLast(new HttpServerCodec()
                    , new HttpObjectAggregator(webSocket.getMaxContentLength())
                    , new WebSocketServerProtocolHandler(webSocket.getPath(), null, true));
            ch.pipeline()
                    .addLast(new ChunkedWriteHandler())
                    .addLast(new WebSocketFrameDecoderHandler())
//                    .addLast(new ByteToGameMessageDecoder(context))
                    .addLast(new WebSocketFrameEncoderHandler())
                    .addLast(new GameMessageToByteEncoder(context))
                    .addLast(getIoHandler())
            ;

//                    .addAfter("decoder", "websocketDecoder",new WebSocketFrameDecoderHandler());
//            ch.pipeline()
//                    .addAfter("serverIdleHandler", "httpCoder", new HttpServerCodec())
//                    .addAfter("httpCoder", "agregator", new HttpObjectAggregator(webSocket.getMaxContentLength()))
//                    .addAfter("agregator", "websocketHandler", new WebSocketServerProtocolHandler(webSocket.getPath(), null, true))
//                    .addLast(new WebSocketFrameDecoderHandler())
//                    .addAfter("encoder", "websocketEncoder", new WebSocketFrameEncoderHandler());
        }
    }
}
