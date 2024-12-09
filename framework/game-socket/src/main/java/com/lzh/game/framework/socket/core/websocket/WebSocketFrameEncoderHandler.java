package com.lzh.game.framework.socket.core.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * @author zehong.l
 * @since 2024-11-26 12:14
 **/
public class WebSocketFrameEncoderHandler extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        list.add(new BinaryWebSocketFrame(buf.retain()));
//        list.add(new TextWebSocketFrame(Unpooled.wrappedBuffer("hello, world!".getBytes())));
    }
}
