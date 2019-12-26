package com.lzh.game.repository;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "game.repository")
public class GameRepositoryConfig {

    private boolean clearRedisAfterClose = true;
    /**
     * Use {@link com.lzh.game.repository.db.persist.RedisPersistConsumer}
     */
    private String redisPersistTop = "persist_queue";

    /**
     * Use {@link com.lzh.game.repository.db.persist.LocationMemoryPersistQueue}
     * each {@link #consumeIntervalTime } will consume queue
     */
    private long consumeIntervalTime = TimeUnit.MINUTES.toMillis(5);


}
