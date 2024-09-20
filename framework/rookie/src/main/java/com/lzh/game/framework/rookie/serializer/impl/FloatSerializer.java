package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;

/**
 * @author zehong.l
 * @since 2024-09-06 11:29
 **/
public class FloatSerializer implements Serializer<Float> {

    @Override
    public Float readObject(ByteBuf in, Class<Float> clz) {
        return ByteBufUtils.readFloat32(in);
    }

    @Override
    public void writeObject(ByteBuf out, Float o) {
        ByteBufUtils.writeFloat32(out, o);
    }
}
