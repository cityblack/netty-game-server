package com.lzh.game.resource.data;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用cas控制资源读取. 当调用{@link #reload()}或是{@link #reload(Class)}, 会将class对应
 * 的原子变量更改成写操作中{@link #WRITING}
 * 读取资源时若为写状态, 则会自旋转等待写状态完成
 */
public class ConcurrentResourceManageHandler implements ResourceManageHandler {
    // Fact option handler
    private ResourceManageHandler handler;
    /**
     * 使用Atomic减小锁颗粒度
     */
    private Map<Class<?>, AtomicBoolean> status;

    private ResourceModelFactory factory;

    private final boolean WRITING = Boolean.TRUE;

    public ConcurrentResourceManageHandler(ResourceModelFactory factory, ResourceManageHandler handler) {
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
    public <T> List<T> findByIndex(Class<T> clazz, String index, Object value) {
        writingWait(clazz);
        return handler.findByIndex(clazz, index, value);
    }

    @Override
    public <T> T findOne(Class<T> clazz, String uniqueIndex, Object value) {
        writingWait(clazz);
        return handler.findOne(clazz, uniqueIndex, value);
    }

    @Override
    public <T, K> T findById(Class<T> clazz, K k) {
        writingWait(clazz);
        return handler.findById(clazz, k);
    }

    @Override
    public void reload() {
        this.status.forEach((k,v) -> setStatus(v, WRITING));
        handler.reload();
        this.status.forEach((k,v) -> setStatus(v, !WRITING));
    }

    @Override
    public void reload(Class<?> clazz) {
        AtomicBoolean status = getStatus(clazz);
        setStatus(status, WRITING);
        handler.reload(clazz);
        setStatus(status, !WRITING);
    }

    /**
     * 获取当前读取状态
     * @param clazz
     * @return
     */
    private AtomicBoolean getStatus(Class<?> clazz) {
        return this.status.get(clazz);
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
        while (!status.compareAndSet(!WRITING, !WRITING)) {}
    }
}
