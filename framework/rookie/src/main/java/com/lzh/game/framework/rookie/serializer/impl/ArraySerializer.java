package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import com.lzh.game.framework.rookie.Rookie;
import com.lzh.game.framework.rookie.utils.Constant;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-13 17:45
 **/
public class ArraySerializer<T> extends ElementSerializer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T readObject(ByteBuf in, Class<T> clz) {
        var len = ByteBufUtils.readRawVarint32(in);
        if (len == 0) {
            return null;
        }
        var classInfo = readClassInfo(in);
        Class<?> target = classInfo.getClz();
        if (Objects.equals(target, byte.class)) {
            return (T) ByteBufUtils.readBytes(in, len);
        }
        T array = (T) Array.newInstance(target, len);
        for (int i = 0; i < len; i++) {
            byte sign = ByteBufUtils.readInt8(in);
            if (sign == Constant.NONE) {
                continue;
            }
            var element = classInfo.getSerializer()
                    .readObject(in, target);
            Array.set(array, i, element);
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeObject(ByteBuf out, T o) {
        var len = Objects.isNull(o) ? 0 : Array.getLength(o);
        ByteBufUtils.writeRawVarint32(out, len);
        if (len == 0) {
            return;
        }
        Class<?> clz = o.getClass().getComponentType();
        if (Objects.equals(clz, byte.class)) {
            ByteBufUtils.writeBytes(out, (byte[]) o);
        } else {
            var classInfo = rookie.getClassInfo(factClass(o, len));
            writeClassInfo(out, classInfo);

            for (int i = 0; i < len; i++) {
                var element = Array.get(o, i);
                ByteBufUtils.writeInt8(out, Constant.getValueSign(element));
                if (Objects.nonNull(element)) {
                    classInfo.getSerializer().writeObject(out, element);
                }
            }
        }
    }

    private Class<?> factClass(T o, int len) {
        var first = Array.get(o, 0);
        if (Objects.nonNull(first)) {
            return first.getClass();
        }
        for (int i = 1; i < len; i++) {
            var element = Array.get(o, i);
            if (Objects.isNull(element)) {
                continue;
            }
            return element.getClass();
        }
        return o.getClass().getComponentType();
    }
}
