package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.Rookie;
import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-13 16:44
 **/
public class EnumSerializer implements Serializer<Enum<?>> {

    @Override
    public Enum<?> readObject(ByteBuf in, Class<Enum<?>> clz) {
        var ordinal = ByteBufUtils.readInt32(in);
        return ordinal == -1 ? null : clz.getEnumConstants()[ordinal];
    }

    @Override
    public void writeObject(ByteBuf out, Enum<?> data) {
        if (Objects.isNull(data)) {
            ByteBufUtils.writeInt32(out, -1);
        } else {
            ByteBufUtils.writeInt32(out, data.ordinal());
        }
    }
}
