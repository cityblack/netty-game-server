package com.lzh.game.framework.repository;

import com.lzh.game.framework.repository.db.persist.LocationMemoryPersistQueue;
import com.lzh.game.framework.repository.db.persist.RedisPersistConsumer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "game.repository")
public class GameRepositoryConfig {

    private boolean clearCacheAfterClose = true;
    /**
     * Use {@link RedisPersistConsumer}
     */
    private String redisPersistTop = "persist_queue";

    /**
     * Use {@link LocationMemoryPersistQueue}
     * each {@link #consumeIntervalTime } will consume queue
     */
    private long consumeIntervalTime = TimeUnit.MINUTES.toMillis(5);


}
