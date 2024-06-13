package com.lzh.game.framework.resource.data;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 使用cas控制资源读取. 当调用{@link #reload()}或是{@link #reload(Class[])}}, 会将class对应
 * 的原子变量更改成写操作中{@link #WRITING}
 * 读取资源时若为写状态, 则会自旋转等待写状态完成
 */
public class ConcurrentResourceManageHandler implements ResourceManageHandle {
    // Fact option handler
    private ResourceManageHandle handler;
    /**
     * Use AtomicBoolean to reduce the force of the lock
     */
    private Map<Class<?>, AtomicBoolean> status;

    private ResourceModelMeta factory;

    private final boolean WRITING = Boolean.TRUE;

    public ConcurrentResourceManageHandler(ResourceModelMeta factory, ResourceManageHandle handler) {
        this.handler = handler;
        this.factory = factory;
        this.init();
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        writingWait(clazz);
        return handler.findAll(clazz);
    }

    @Override
    public <T> List<T> findByIndex(Class<T> clazz, String index, Serializable value) {
        writingWait(clazz);
        return handler.findByIndex(clazz, index, value);
    }

    @Override
    public <T> T findOne(Class<T> clazz, String uniqueIndex, Serializable value) {
        writingWait(clazz);
        return handler.findOne(clazz, uniqueIndex, value);
    }

    @Override
    public <T> T findById(Class<T> clazz, Serializable k) {
        writingWait(clazz);
        return handler.findById(clazz, k);
    }

    @Override
    public void reload() {
        try {
            this.status.forEach((k,v) -> setStatus(v, WRITING));
            handler.reload();
        } finally {
            this.status.forEach((k,v) -> setStatus(v, !WRITING));
        }

    }

    @Override
    public void reload(Class<?>[] clazz) {
        try {
            for (Class<?> c: clazz) {
                setStatus(getStatus(c), WRITING);
            }
            handler.reload(clazz);
        } finally {
            for (Class<?> c: clazz) {
                setStatus(getStatus(c), !WRITING);
            }
        }
    }

    /**
     * 获取当前读取状态
     * @param clazz
     * @return
     */
    private AtomicBoolean getStatus(Class<?> clazz) {
        return this.status.computeIfAbsent(clazz, c -> new AtomicBoolean(false));
    }

    private void setStatus(AtomicBoolean status, boolean value) {
        status.getAndSet(value);
    }

    public void init() {
        Map<Class<?>, AtomicBoolean> map = new HashMap<>();
        factory.forEach(e -> map.put(e.getDataType(), new AtomicBoolean(!WRITING)));
        this.status = map;
    }

    private void writingWait(Class<?> clazz) {
        AtomicBoolean status = getStatus(clazz);
        while (!status.compareAndSet(!WRITING, !WRITING)) {
            LockSupport.parkNanos(100L);
        }
    }
}
