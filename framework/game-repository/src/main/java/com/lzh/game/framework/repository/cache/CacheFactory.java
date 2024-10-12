package com.lzh.game.framework.repository.cache;

import com.lzh.game.framework.repository.cache.Cache;

/**
 * @author zehong.l
 * @since 2024-10-12 11:56
 **/
public interface CacheFactory {

    Cache createCache(String name, Class<?> type);
}
