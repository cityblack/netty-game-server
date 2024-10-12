package com.lzh.game.framework.repository.cache;

/**
 * @author zehong.l
 * @since 2024-10-12 11:49
 **/
public interface Cache {
    /**
     * Return the value to which this cache maps the specified key.
     * @param key – the key whose associated value is to be returned
     * @return the value to which this cache maps the specified key (which may be null itself)
     */
    Object get(Object key);

    void put(Object key, Object value);

    void evict(Object key);

    void clear();
}
