package com.lzh.game.resource.data.cache;

import com.lzh.game.resource.data.ResourceModel;

import java.io.Serializable;
import java.util.List;

/**
 * Resource cache
 * In local memory or redis?
 * @param <K> cache key
 * @param <T> cache data
 */
public interface ResourceCache<K extends Serializable, T> {

    List<T> findAll();

    List<T> findByIndex(String indexName, Serializable value);

    T findById(K k);

    T findOne(String indexName, Serializable value);

    void put(T data, ResourceModel resourceModel);

    void clear();
}
