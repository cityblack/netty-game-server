package com.lzh.game.start.model.core.util.condition;

import com.lzh.game.start.model.i18n.RequestException;

public interface Condition<T> {

    boolean verify(T param);

    void verifyThrow(T param) throws RequestException;
}
