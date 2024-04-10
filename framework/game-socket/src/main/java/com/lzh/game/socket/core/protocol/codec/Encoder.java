package com.lzh.game.socket.core.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface Encoder {

    void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception;
}
