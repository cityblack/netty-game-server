package com.lzh.game.framework.repository.config;

import com.lzh.game.framework.repository.anno.CachedModel;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-10-12 14:50
 **/
public class ComposeConfig {

    private RepositoryConfig config;

    private CachedModel model;

    public ComposeConfig(RepositoryConfig config, CachedModel model) {
        this.config = config;
        this.model = model;
    }

    public ComposeConfig(RepositoryConfig config, Class<?> type) {
        this(config, CachedModel.getModel(type));
    }

    public long getPersistenceInterval() {
        return Objects.isNull(model) ? config.getPersistenceInterval()
                : model.getPersistenceInterval();
    }

    public int getCacheSize() {
        return Objects.isNull(model) ? config.getCacheSize() : model.getSize();
    }

    public long getCacheExpire() {
        return Objects.isNull(model) ? config.getCacheExpire() : model.getExpire();
    }
}
