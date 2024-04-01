package com.lzh.game.resource.data;

import java.io.Serializable;

/**
 * Resource classes' field and method value
 */
public interface Getter {
    /**
     * Getter field or method value
     * @param bean resource instantiate
     * @return
     */
    Serializable get(Object bean);

    Class<?> getType();

    String name();
}
