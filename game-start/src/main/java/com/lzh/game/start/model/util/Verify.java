package com.lzh.game.start.model.util;


import com.lzh.game.start.model.i18n.RequestException;

public interface Verify {
    /**
     *
     * @param param
     * @throws RequestException
     */
    default void verifyThrow(Object param) {
        verifyThrow(param, 1);
    };
    /**
     * Verify item is enough or not
     * @param param
     * @param multiple - item num
     * @throws RequestException - throw when item isn't enough
     */
    void verifyThrow(Object param, int multiple) throws RequestException;

    /**
     * Verify item is enough or not
     * @param param
     * @param multiple
     * @return - is enough or not
     */
    boolean verify(Object param, int multiple);

    /**
     *
     * @param param
     * @return
     */
    default boolean verify(Object param) {
        return verify(param, 1);
    }
}
