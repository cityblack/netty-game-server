package com.lzh.game.framework.repository.anno;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-10-12 12:27
 **/
@Getter
public class CachedModel {

    private Class<?> type;

    private int size;

    private long persistenceInterval;

    private long expire;

    public static CachedModel getModel(Class<?> type) {
        if (!isCacheModel(type)) {
            return null;
        }
        return CacheModelManager.INSTANCE.getCacheModel(type);
    }

    private static CachedModel of(Class<?> type) {
        Cached cached = type.getAnnotation(Cached.class);
        if (Objects.isNull(cached)) {
            return null;
        }
        CachedModel model = new CachedModel();
        model.type = type;
        model.size = cached.size();
        model.persistenceInterval = cached.persistenceInterval();
        model.expire = cached.expire();
        return model;
    }

    public static boolean isCacheModel(Class<?> type) {
        return type.isAnnotationPresent(Cached.class);
    }

    static class CacheModelManager {
        static final Map<Class<?>, CachedModel> CACHED = new HashMap<>();

        static final CacheModelManager INSTANCE = new CacheModelManager();

        public CachedModel getCacheModel(Class<?> type) {
            var model = CACHED.get(type);
            if (Objects.isNull(model)) {
                synchronized (this) {
                    model = CACHED.get(type);
                    if (Objects.isNull(model)) {
                        model = CachedModel.of(type);
                        CACHED.put(type, model);
                    }
                }
            }
            return model;
        }
    }
}
