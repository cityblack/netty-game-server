package com.lzh.game.resource;

import java.util.List;

public interface Storage<K, V> {
    /**
     * Get all data
     * @return
     */
    List<V> getAll();

    /**
     * Get data by uniqueIndex
     * @param UniqueIndex
     * @param value
     * @return
     */
    V getUnique(String UniqueIndex, Object value);

    /**
     *
     * @param index
     * @param value
     * @return
     */
    List<V> getIndex(String index, Object value);

    /**
     * Get data by id
     * @param k
     * @return
     */
    V get(K k);

    /**
     * throw {@link NullPointerException} when data is't exist
     * @param k
     */
    V getOrThrow(K k);
}
