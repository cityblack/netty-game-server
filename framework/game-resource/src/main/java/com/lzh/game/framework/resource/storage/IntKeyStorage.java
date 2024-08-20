package com.lzh.game.framework.resource.storage;

/**
 * Reduce autoboxing
 * Reduce mem
 *
 * @author zehong.l
 * @since 2024-08-20 12:02
 **/
public interface IntKeyStorage<V> extends Storage<Integer, V> {

    V get(int id);

}
