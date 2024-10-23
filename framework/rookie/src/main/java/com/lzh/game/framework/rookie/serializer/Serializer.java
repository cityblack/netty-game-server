package com.lzh.game.framework.rookie.serializer;


import com.lzh.game.framework.rookie.Rookie;
import io.netty.buffer.ByteBuf;

/**
 * Auto boxing is a problem, but not fatal
 *
 * @author zehong.l
 * @since 2024-08-28 16:32
 **/
public interface Serializer<T> {

   T readObject(ByteBuf in, Class<T> clz);

    void writeObject(ByteBuf out, T o);

    default void initialize(Class<T> clz, Rookie rookie) {

    }
}
