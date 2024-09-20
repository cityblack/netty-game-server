package com.lzh.game.framework.rookie.serializer.impl.collection;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zehong.l
 * @since 2024-09-13 17:24
 **/
public class QueueSerializer<T extends Queue<Object>>
        extends AbstractCollectionSerializer<T> {

    @Override
    protected Collection<Object> newContain(int len, Class<T> clz) {
        if (isSameType(clz, Queue.class) || isSameType(clz, AbstractQueue.class)) {
            return new LinkedList<>();
        } else if (isSameType(clz, ArrayBlockingQueue.class)) {
            return new ArrayBlockingQueue<>(len);
        } else if (isSameType(clz, LinkedBlockingQueue.class)) {
            return new LinkedBlockingQueue<>();
        }
        return newBean(clz);
    }
}
