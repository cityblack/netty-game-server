package com.lzh.game.resource.data;

/**
 * Resource classes' field and method value
 */
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
