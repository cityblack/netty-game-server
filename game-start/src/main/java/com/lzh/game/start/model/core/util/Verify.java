package com.lzh.game.start.model.core.util;


import com.lzh.game.start.model.i18n.RequestException;

public interface Verify<T> {
    /**
     *
     * @param param
     * @throws RequestException
     */
    default void verifyThrow(T param) {
        verifyThrow(param, 1);
    };
    /**
     * Verify item is enough or not
     * @param param
     * @param multiple - item num
     * @throws RequestException - throw when item isn't enough
     */
    void verifyThrow(T param, int multiple) throws RequestException;

    /**
     * Verify item is enough or not
     * @param param
     * @param multiple
     * @return - is enough or not
     */
    boolean verify(T param, int multiple);

    /**
     *
     * @param param
     * @return
     */
    default boolean verify(T param) {
        return verify(param, 1);
    }
}
