package com.lzh.game.common.scoket.session.cache;


import com.lzh.game.common.scoket.session.Session;

import java.util.Objects;

public interface SessionMemoryCache<K, V extends Session> {

    String SESSION_PREFIX = "GameSession_";

    void put(K key, V value);

    V get(K key);

    /**
     * if key not exist
     *
     * @param key
     * @return
     * @throws NullPointerException
     */
    default V getThrowException(K key) throws NullPointerException {

        return Objects.requireNonNull(get(key));
    }

    boolean containKey(K key);

    V remove(K key);

    String getKey(K key);

    void clean();
}
