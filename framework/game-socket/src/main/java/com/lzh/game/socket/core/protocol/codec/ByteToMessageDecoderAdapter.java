package com.lzh.game.socket.core.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteToMessageDecoderAdapter extends ByteToMessageDecoder {

    private Decoder decoder;

    public ByteToMessageDecoderAdapter(Decoder decoder) {
        this.decoder = decoder;
    }

    public ByteToMessageDecoderAdapter() {
        this(new MessageDecoder());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        decoder.decode(ctx, in, out);
    }

}
