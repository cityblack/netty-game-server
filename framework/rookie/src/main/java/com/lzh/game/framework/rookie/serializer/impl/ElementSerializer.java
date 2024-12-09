package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.Rookie;
import com.lzh.game.framework.rookie.serializer.ClassInfo;
import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-13 17:27
 **/
public abstract class ElementSerializer<T> implements Serializer<T> {

    protected Rookie rookie;

    @Override
    public void initialize(Class<T> clz, Rookie rookie) {
        this.rookie = rookie;
    }

    protected void writeClassInfo(ByteBuf out, ClassInfo classInfo) {
        ByteBufUtils.writeInt16(out, classInfo.getId());
    }

    // support abstract class. write impl class id to byte
    @SuppressWarnings("unchecked")
    protected void writeElement(ByteBuf out, Object element) {
        if (Objects.isNull(element)) {
            var classInfo = rookie.getClassInfo(Void.TYPE);
            writeClassInfo(out, classInfo);
            return;
        }
        var target = element.getClass().isArray() ? rookie.getArrayClassInfo()
                : rookie.getClassInfo(element.getClass());
        writeClassInfo(out, target);
        this.rookie.serializer(out, element);
    }

    protected ClassInfo readClassInfo(ByteBuf in) {
        var id = ByteBufUtils.readInt16(in);
        return rookie.getClassInfo(id);
    }

    @SuppressWarnings("unchecked")
    protected Object readElement(ByteBuf in) {
        ClassInfo info = readClassInfo(in);
        if (info.getClz() == Void.TYPE) {
            return null;
        }
        return this.rookie.deserializer(in, info.getClz());
    }

    protected T newBean(Class<T> clz) {
        try {
            return clz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("New bean error", e);
        }
    }

    protected int getCollectionSize(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty() ? 0 : collection.size();
    }

    protected int getCollectionSize(Map<?, ?> map) {
        return Objects.isNull(map) || map.isEmpty() ? 0 : map.size();
    }

    protected boolean isSameType(Class<T> clz, Class<?> target) {
        return Objects.equals(clz, target);
    }
}

