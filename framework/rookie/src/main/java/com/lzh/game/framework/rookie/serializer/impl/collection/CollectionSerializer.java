package com.lzh.game.framework.rookie.serializer.impl.collection;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * rule:
 * len|id|element|id|element
 *
 * @author zehong.l
 * @since 2024-09-06 11:30
 **/
public class CollectionSerializer<T extends Collection<Object>>
        extends AbstractCollectionSerializer<T> {

    @Override
    protected Collection<Object> newContain(int len, Class<T> clz) {
        if (isSameType(clz, Collection.class) || isSameType(clz, List.class)
                || isSameType(clz, ArrayList.class) || isSameType(clz, AbstractList.class)
                || isSameType(clz, AbstractCollection.class)) {
            return new ArrayList<>();
        }
        if (isSameType(clz, LinkedList.class)) {
            return new LinkedList<>();
        }
        if (isSameType(clz, CopyOnWriteArrayList.class)) {
            return new CopyOnWriteArrayList<>();
        }
        if (isSameType(clz, Vector.class)) {
            return new Vector<>();
        }
        return super.newContain(len, clz);
    }
}
