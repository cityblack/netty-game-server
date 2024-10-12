package com.lzh.game.framework.repository.config;

import com.lzh.game.framework.repository.Constant;
import com.lzh.game.framework.repository.persist.queue.LocationMemoryPersist;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "game.repository")
public class RepositoryConfig {

    private boolean clearCacheAfterClose = true;

    private String persistTop = "persist_queue";

    /**
     * Use {@link LocationMemoryPersist}
     * each {@link #persistenceInterval } will consume queue
     */
    private long persistenceInterval = Constant.PERSISTENCE_INTERVAL;

    private int cacheSize = Constant.CACHE_SIZE;

    private long cacheExpire = Constant.CACHE_EXPIRE;
}
