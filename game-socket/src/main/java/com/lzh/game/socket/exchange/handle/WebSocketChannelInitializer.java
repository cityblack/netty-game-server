package com.lzh.game.socket.exchange.handle;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.lzh.game.socket.dispatcher.DispatcherHandler;
import com.lzh.game.socket.exchange.coder.ExchangeProtocol;
import com.lzh.game.socket.exchange.session.ChannelSessionManage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private DispatcherHandler dispatcher;
    @Autowired
    private ChannelSessionManage sessionManage;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addFirst(new LoggingHandler(LogLevel.DEBUG))
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new WebSocketServerCompressionHandler())
                .addLast(new WebSocketServerProtocolHandler("/ws", null, true))
                .addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                        ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                        objs.add(buf);
                        buf.retain();
                    }
                })
                .addLast(new WebSocketServerCompressionHandler())
                .addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                        ByteBuf result = null;
                        if (msg instanceof MessageLite) {
                            result = wrappedBuffer(((MessageLite) msg).toByteArray());
                        }
                        if (msg instanceof MessageLite.Builder) {
                            result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                        }
                        WebSocketFrame frame = new BinaryWebSocketFrame(result);
                        out.add(frame);
                    }
                })
                .addLast(new ProtobufDecoder(ExchangeProtocol.Request.getDefaultInstance()))
                .addLast(new GameHandler(dispatcher,sessionManage))
                .addLast(new IdleStateHandler(0,0,180,TimeUnit.SECONDS));

    }

}
