package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-13 16:50
 **/
public class ShotSerializer implements Serializer<Short> {

    @Override
    public Short readObject(ByteBuf in, Class<Short> clz) {
        return ByteBufUtils.readInt16(in);
    }

    @Override
    public void writeObject(ByteBuf out, Short o) {
        ByteBufUtils.writeInt16(out, o);
    }
}
