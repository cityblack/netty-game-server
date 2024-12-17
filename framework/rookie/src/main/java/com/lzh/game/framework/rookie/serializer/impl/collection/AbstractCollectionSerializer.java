package com.lzh.game.framework.rookie.serializer.impl.collection;

import com.lzh.game.framework.rookie.serializer.impl.ElementSerializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import com.lzh.game.framework.rookie.Rookie;
import io.netty.buffer.ByteBuf;

import java.util.Collection;

/**
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
        for (int i = 0; i < len; i++) {
            collection.add(readElement(in));
        }
        return (T) collection;
    }

    @Override
    public void writeObject(ByteBuf out, T collection) {
        int len = getCollectionSize(collection);
        ByteBufUtils.writeRawVarint32(out, len);
        if (len > 0) {
            for (Object el : collection) {
                writeElement(out, el);
            }
        }
    }

    // reduce reflection
    protected Collection<Object> newContain(int len, Class<T> clz) {
        return newBean(clz);
    }
}
