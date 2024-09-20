package com.lzh.game.framework.rookie.serializer.impl.collection;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author zehong.l
 * @since 2024-09-13 17:04
 **/
public class SetSerializer<T extends Set<Object>>
        extends AbstractCollectionSerializer<T> {

    @Override
    protected Collection<Object> newContain(int len, Class<T> clz) {
        if (isSameType(clz, Set.class) || isSameType(clz, HashSet.class)
                || isSameType(clz, AbstractSet.class)) {
            return new HashSet<>();
        } else if (isSameType(clz, TreeSet.class)) {
            return new TreeSet<>();
        } else if (isSameType(clz, LinkedHashSet.class)) {
            return new LinkedHashSet<>();
        } else if (isSameType(clz, ConcurrentSkipListSet.class)) {
            return new ConcurrentSkipListSet<>();
        }
        return super.newContain(len, clz);
    }
}
