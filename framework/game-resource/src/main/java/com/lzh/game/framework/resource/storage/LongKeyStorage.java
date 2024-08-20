package com.lzh.game.framework.resource.storage;

/**
 * Reduce autoboxing
 *
 * @author zehong.l
 * @since 2024-08-20 12:04
 **/
public interface LongKeyStorage<V> extends Storage<Long, V> {

    V get(long id);

}
