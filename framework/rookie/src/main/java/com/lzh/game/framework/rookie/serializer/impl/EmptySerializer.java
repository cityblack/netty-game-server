package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-18 18:31
 **/
public class EmptySerializer implements Serializer {

    @Override
    public Object readObject(ByteBuf in, Class clz) {
        return null;
    }

    @Override
    public void writeObject(ByteBuf out, Object o) {

    }
}
