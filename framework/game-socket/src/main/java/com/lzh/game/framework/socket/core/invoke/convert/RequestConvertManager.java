package com.lzh.game.framework.socket.core.invoke.convert;

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
