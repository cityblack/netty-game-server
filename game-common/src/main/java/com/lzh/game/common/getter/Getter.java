package com.lzh.game.common.getter;

public interface Getter {
    /**
     * Getter field or method value
     * @param bean resource instantiate
     * @return
     */
    Object get(Object bean);

    Class<?> getType();

    String name();
}
