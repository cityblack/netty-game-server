package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-06 11:21
 **/
public class IntSerializer implements Serializer<Integer> {

    @Override
    public Integer readObject(ByteBuf in, Class<Integer> clz) {
        return ByteBufUtils.readInt32(in);
    }

    @Override
    public void writeObject(ByteBuf out, Integer o) {
        out.ensureWritable(Integer.BYTES + 1);
        ByteBufUtils.writeInt32(out, o);
    }
}
