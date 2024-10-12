package com.lzh.game.framework.repository.persist.queue;

import com.lzh.game.framework.repository.config.ComposeConfig;
import com.lzh.game.framework.repository.config.RepositoryConfig;
import com.lzh.game.framework.repository.persist.Persist;
import com.lzh.game.framework.repository.persist.PersistFactory;
import com.lzh.game.framework.repository.persist.consumer.PersistConsumer;

/**
 * @author zehong.l
 * @since 2024-10-12 15:47
 **/
public class LocationMemoryPersistFactory implements PersistFactory {

    private RepositoryConfig config;

    private PersistConsumer consumer;

    public LocationMemoryPersistFactory(RepositoryConfig config, PersistConsumer consumer) {
        this.config = config;
        this.consumer = consumer;
    }

    @Override
    public Persist createPersist(Class<?> type) {
        var compose = new ComposeConfig(config, type);
        return new LocationMemoryPersist(type, consumer, compose.getPersistenceInterval());
    }
}
