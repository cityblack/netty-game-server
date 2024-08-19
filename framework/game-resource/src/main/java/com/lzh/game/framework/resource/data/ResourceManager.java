package com.lzh.game.framework.resource.data;

import java.io.Serializable;
import java.util.List;

/**
 * Static resource getting manage
 */
public interface ResourceManager {

    <T> List<T> findAll(Class<T> clazz);

    <T> List<T> findByIndex(Class<T> clazz, String indexName, Serializable value);

    <T> T findOne(Class<T> clazz, String indexName, Serializable value);

    <T> T findById(Class<T> clazz, Serializable k);

    void reload();

    void reload(Class<?>[] clazz);
}
