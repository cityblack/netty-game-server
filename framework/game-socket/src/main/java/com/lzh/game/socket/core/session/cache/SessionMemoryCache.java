package com.lzh.game.socket.core.session.cache;

import com.lzh.game.socket.core.session.Session;

import java.util.List;
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

    List<V> getAll();
}
