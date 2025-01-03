package com.lzh.game.start.model.util.condition;

import com.lzh.game.start.model.i18n.RequestException;

public interface Condition {

    boolean verify(Object param);

    void verifyThrow(Object param) throws RequestException;
}
