package com.lzh.game.repository.cache;

import java.io.Serializable;

public interface CacheEntity<PK extends Serializable & Comparable<PK>> {

    PK cacheKey();
}
