package com.lzh.game.socket.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

public interface Encoder {

    void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception;
}
