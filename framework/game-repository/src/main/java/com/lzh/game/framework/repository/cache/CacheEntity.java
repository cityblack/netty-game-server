package com.lzh.game.framework.repository.cache;

import java.io.Serializable;

/**
 * Cache Object
 * @param <PK>
 */
public interface CacheEntity<PK extends Serializable & Comparable<PK>> {

    PK cacheKey();
}
