package com.lzh.game.socket.core.invoke;

public interface RequestConvertManager {

    /**
     *
     * @param target
     * @param convert
     */
    void registerConvert(Class<?> target, RequestConvert<?> convert);

    RequestConvert<?> getConvert(Class<?> clazz);

    boolean hasConvert(Class<?> target);

    RequestConvert<?> getOrCreateDefaultConvert(Class<?> clazz);
}
