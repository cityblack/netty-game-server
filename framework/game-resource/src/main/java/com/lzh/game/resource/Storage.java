package com.lzh.game.resource;

import java.io.Serializable;
import java.util.List;

public interface Storage<K extends Serializable, V> {
    /**
     * Get all data
     * @return
     */
    List<V> getAll();

    /**
     * Get data by uniqueIndex
     * @param UniqueIndexName
     * @param value
     * @return
     */
    V getUnique(String UniqueIndexName, Serializable value);

    /**
     *
     * @param indexName
     * @param value
     * @return
     */
    List<V> getIndex(String indexName, Serializable value);

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
