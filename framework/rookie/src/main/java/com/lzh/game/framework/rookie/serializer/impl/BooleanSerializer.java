package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-13 16:30
 **/
public class BooleanSerializer implements Serializer<Boolean> {

    @Override
    public Boolean readObject(ByteBuf in, Class<Boolean> clz) {
        return ByteBufUtils.readBoolean(in);
    }

    @Override
    public void writeObject(ByteBuf out, Boolean o) {
        ByteBufUtils.writeBoolean(out, o);
    }
}
