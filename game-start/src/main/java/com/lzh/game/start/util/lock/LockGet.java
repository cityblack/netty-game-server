package com.lzh.game.start.util.lock;

/**
 *
 * @param <T>
 */
public interface LockGet<T> {
    /**
     * 上锁时长
     * @param time
     * @return
     */
    T lockAndGet(long time);

    /**
     * 解锁
     */
    void unlock();

    /**
     *
     * @return
     */
    T lockAndGet();
}
