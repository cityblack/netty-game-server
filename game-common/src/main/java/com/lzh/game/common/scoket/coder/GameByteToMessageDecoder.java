package com.lzh.game.common.scoket.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class GameByteToMessageDecoder extends ByteToMessageDecoder {

    private Decoder decoder;

    public GameByteToMessageDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public GameByteToMessageDecoder() {
        this(new DefaultGameDecoder());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        decoder.decode(ctx, in, out);
    }

}
