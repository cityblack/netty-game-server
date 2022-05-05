package com.lzh.game.socket.core.invoke;

import java.util.Set;

public interface ConvertManager {

    default boolean registerConvert(Class<?> target, ParamConvert<?> convert) {
        return registerConvert(target, convert, false);
    }

    /**
     *
     * @param target
     * @param convert
     * @param inner -- is not proto param
     * @return
     */
    boolean registerConvert(Class<?> target, ParamConvert<?> convert, boolean inner);

    ParamConvert<?> getConvert(Class<?> clazz);

    boolean isInnerConvert(Class<?> clazz);

    Set<Class<?>> inner();

    boolean hasConvert(Class<?> target);
}
