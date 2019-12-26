package com.lzh.game.resource.data;

import java.util.List;

/**
 * Static resource getting manage
 */
public interface ResourceManageHandler {

    <T> List<T> findAll(Class<T> clazz);

    <T> List<T> findByIndex(Class<T> clazz, String index, Object value);

    <T> T findOne(Class<T> clazz, String uniqueIndex, Object value);

    <T,K> T findById(Class<T> clazz, K k);

    void reload();

    void reload(Class<?> clazz);
}
