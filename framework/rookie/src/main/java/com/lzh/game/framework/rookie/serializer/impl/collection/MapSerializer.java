package com.lzh.game.framework.rookie.serializer.impl.collection;

import com.lzh.game.framework.rookie.serializer.impl.ElementSerializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.jar.Attributes;

/**
 * Rule:
 * len|(key)id|element|(value)id|element
 *
 * @author zehong.l
 * @since 2024-09-11 16:40
 **/
public class MapSerializer<T extends Map<Object, Object>>
        extends ElementSerializer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T readObject(ByteBuf in, Class<T> clz) {
        int len = ByteBufUtils.readRawVarint32(in);
        var map = newMap(clz);
        for (int i = 0; i < len; i++) {
            map.put(readElement(in), readElement(in));
        }
        return (T) map;
    }

    @Override
    public void writeObject(ByteBuf out, T map) {
        int len = getCollectionSize(map);
        ByteBufUtils.writeRawVarint32(out, len);
        if (len > 0) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                writeElement(out, entry.getKey());
                writeElement(out, entry.getValue());
            }
        }
    }

    private Map<Object, Object> newMap(Class<T> clz) {
        if (isSameType(clz, Map.class) || isSameType(clz, HashMap.class)
                || isSameType(clz, AbstractMap.class)) {
            return new HashMap<>();
        } else if (isSameType(clz, TreeMap.class)) {
            return new TreeMap<>();
        } else if (isSameType(clz, LinkedHashMap.class)) {
            return new LinkedHashMap<>();
        } else if (isSameType(clz, ConcurrentHashMap.class)) {
            return new ConcurrentHashMap<>();
        } else if (isSameType(clz, ConcurrentSkipListMap.class)) {
            return new ConcurrentSkipListMap<>();
        } else if (isSameType(clz, IdentityHashMap.class)) {
            return new IdentityHashMap<>();
        } else if (isSameType(clz, WeakHashMap.class)) {
            return new WeakHashMap<>();
        } else if (isSameType(clz, Attributes.class)) {
            return new Attributes();
        }
        return newBean(clz);
    }
}
