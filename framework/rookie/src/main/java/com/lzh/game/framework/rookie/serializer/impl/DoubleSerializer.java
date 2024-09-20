package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-06 11:29
 **/
public class DoubleSerializer implements Serializer<Double> {
    @Override
    public Double readObject(ByteBuf in, Class<Double> clz) {
        return ByteBufUtils.readFloat64(in);
    }

    @Override
    public void writeObject(ByteBuf out, Double o) {
        ByteBufUtils.writeFloat64(out, o);
    }
}
