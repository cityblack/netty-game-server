package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-06 11:28
 **/
public class LongSerializer implements Serializer<Long> {
    @Override
    public Long readObject(ByteBuf in, Class<Long> clz) {
        return ByteBufUtils.readInt64(in);
    }

    @Override
    public void writeObject(ByteBuf out, Long o) {
        ByteBufUtils.writeInt64(out, o);
    }
}
