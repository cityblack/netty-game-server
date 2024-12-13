package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-12-10 16:33
 **/
public class ByteSerializer implements Serializer<Byte> {

    @Override
    public Byte readObject(ByteBuf in, Class<Byte> clz) {
        return ByteBufUtils.readInt8(in);
    }

    @Override
    public void writeObject(ByteBuf out, Byte o) {
        ByteBufUtils.writeInt8(out, o);
    }
}
