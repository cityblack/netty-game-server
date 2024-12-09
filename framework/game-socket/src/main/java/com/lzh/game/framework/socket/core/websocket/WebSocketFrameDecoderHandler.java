package com.lzh.game.framework.socket.core.websocket;

import com.lzh.game.framework.socket.core.protocol.Request;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

/**
 * @author zehong.l
 * @since 2024-11-26 12:10
 **/
public class WebSocketFrameDecoderHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof BinaryWebSocketFrame binary) {
            binary.retain();
            ctx.fireChannelRead(frame.content());
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
        } else if (frame instanceof TextWebSocketFrame textWebSocketFrame) {
//            textWebSocketFrame.retain();
            var request = new Request();
            request.setMsgId((short) -10086);
            ctx.fireChannelRead(request);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer("hello, world!xxxx".getBytes())));
    }
}
