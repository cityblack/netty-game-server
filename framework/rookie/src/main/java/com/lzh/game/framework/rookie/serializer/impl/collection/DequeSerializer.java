package com.lzh.game.framework.rookie.serializer.impl.collection;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author zehong.l
 * @since 2024-09-13 17:40
 **/
public class DequeSerializer<T extends Deque<Object>> extends QueueSerializer<T> {

    @Override
    protected Collection<Object> newContain(int len, Class<T> clz) {
        if (isSameType(clz, Deque.class) || isSameType(clz, ArrayDeque.class)) {
            return new ArrayDeque<>();
        }
        if (isSameType(clz, LinkedBlockingDeque.class)) {
            return new LinkedBlockingDeque<>();
        }
        return super.newContain(len, clz);
    }
}
