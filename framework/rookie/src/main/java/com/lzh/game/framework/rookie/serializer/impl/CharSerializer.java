package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-18 15:07
 **/
public class CharSerializer implements Serializer<Character> {

    @Override
    public Character readObject(ByteBuf in, Class<Character> clz) {
        return ByteBufUtils.readChar(in);
    }

    @Override
    public void writeObject(ByteBuf out, Character o) {
        ByteBufUtils.writeChar(out, o);
    }
}
