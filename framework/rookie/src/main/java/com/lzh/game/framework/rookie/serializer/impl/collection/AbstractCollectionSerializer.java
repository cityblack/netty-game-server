package com.lzh.game.framework.rookie.serializer.impl.collection;

import com.lzh.game.framework.rookie.Rookie;
import com.lzh.game.framework.rookie.serializer.ClassInfo;
import com.lzh.game.framework.rookie.serializer.impl.ElementSerializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Objects;

/**
 * Collect Serializer
 * <p>
 * len|id(2B)|element|id|element|id|element
 * <p>
 * If compress class (all element is same class):
 * <p>
 * len|id(2B)|sign(1B null or not)|element
 *
 * @author zehong.l
 * @since 2024-09-13 17:05
 **/
public abstract class AbstractCollectionSerializer<T extends Collection<Object>>
        extends ElementSerializer<T> {

    protected Rookie rookie;

    @Override
    public void initialize(Class<T> clz, Rookie rookie) {
        super.initialize(clz, rookie);
        this.rookie = rookie;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T readObject(ByteBuf in, Class<T> clz) {
        int len = ByteBufUtils.readRawVarint32(in);
        var collection = newContain(len, clz);
        boolean compress = ByteBufUtils.readBoolean(in);
        if (compress) {
            var classInfo = readClassInfo(in);
            for (int i = 0; i < len; i++) {
                collection.add(classInfo.getClz() == Void.class ? null
                        : this.rookie.deserializer(in, classInfo.getClz()));
            }
        } else {
            for (int i = 0; i < len; i++) {
                collection.add(readElement(in));
            }
        }
        return (T) collection;
    }

    @Override
    public void writeObject(ByteBuf out, T collection) {
        int len = getCollectionSize(collection);
        ByteBufUtils.writeRawVarint32(out, len);
        if (len > 0) {
            boolean compress = isCompressClass(rookie, collection);
            ByteBufUtils.writeBoolean(out, compress);
            if (compress) {
                var classInfo = getCompressClassInfo(rookie, collection);
                writeClassInfo(out, classInfo);
                for (Object element : collection) {
                    this.rookie.serializer(out, element);
                }
            } else {
                for (Object el : collection) {
                    writeElement(out, el);
                }
            }
        }
    }

    private ClassInfo getCompressClassInfo(Rookie rookie, T collection) {
        for (var iterator = collection.iterator(); iterator.hasNext(); ) {
            var element = iterator.next();
            if (Objects.isNull(element)) {
                continue;
            }
            return element.getClass().isArray() ? rookie.getArrayClassInfo()
                    : rookie.getClassInfo(element.getClass());
        }
        return rookie.getClassInfo(Void.class);
    }

    protected boolean isCompressClass(Rookie rookie, T collection) {
        int limit = rookie.getConfig().getCompressCollectionValueSize();
        return limit > 0 && collection.size() >= limit;
    }

    // reduce reflection
    protected Collection<Object> newContain(int len, Class<T> clz) {
        return newBean(clz);
    }
}
