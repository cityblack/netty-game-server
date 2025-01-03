package com.lzh.game.start.model.util.consume;

import com.lzh.game.start.model.util.AbstractVerify;

public abstract class AbstractConsume extends AbstractVerify implements Consume {

    public final AbstractConsume parse(String value) {
        doParse(value);
        return this;
    }

    protected abstract void doParse(String value);
}
