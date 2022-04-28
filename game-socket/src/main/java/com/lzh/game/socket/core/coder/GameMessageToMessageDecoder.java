package com.lzh.game.socket.core.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

public class GameMessageToMessageDecoder extends MessageToByteEncoder<Serializable> {

    private Encoder encoder;

    public GameMessageToMessageDecoder() {
        this(new DefaultGameEncoder());
    }

    public GameMessageToMessageDecoder(Encoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        encoder.encode(ctx, msg, out);
    }
}
