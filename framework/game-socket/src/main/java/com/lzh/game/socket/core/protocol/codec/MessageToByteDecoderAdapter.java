package com.lzh.game.socket.core.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

public class MessageToByteDecoderAdapter extends MessageToByteEncoder<Serializable> {

    private Encoder encoder;

    public MessageToByteDecoderAdapter() {
        this(new DefaultGameEncoder());
    }

    public MessageToByteDecoderAdapter(Encoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        encoder.encode(ctx, msg, out);
    }
}
