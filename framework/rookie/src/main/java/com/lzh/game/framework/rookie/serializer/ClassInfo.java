package com.lzh.game.framework.rookie.serializer;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Modifier;

/**
 * @author zehong.l
 * @since 2024-09-06 12:04
 **/
@Getter
@Setter
public class ClassInfo {

    private short id;

    private Class<?> clz;

    private Serializer<?> serializer;

    private Class<?>[] fieldTypes;

    private boolean array;

    private boolean enumType;

    private boolean finalType;

    private boolean collection;

    private volatile boolean init;

    public static ClassInfo of(short id, Class<?> clz, Serializer<?> serializer) {
        var info = new ClassInfo();
        info.id = id;
        info.clz = clz;
        info.serializer = serializer;
        info.init = false;
        info.array = clz.isArray();
        info.enumType = clz.isEnum();
        info.finalType = Modifier.isFinal(clz.getModifiers());
        return info;
    }

    public static ClassInfo array(short id, Class<?> clz, Serializer<?> serializer) {
        var info = new ClassInfo();
        info.id = id;
        info.clz = clz;
        info.serializer = serializer;
        info.init = true;
        info.array = true;
        return info;
    }

    public Serializer getSerializer() {
        return serializer;
    }

}
