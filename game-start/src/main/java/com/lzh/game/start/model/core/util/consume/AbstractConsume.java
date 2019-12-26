package com.lzh.game.start.model.core.util.consume;

import com.lzh.game.start.model.core.util.AbstractVerify;

public abstract class AbstractConsume<T> extends AbstractVerify<T> implements Consume<T> {

    public final AbstractConsume parse(String value) {
        doParse(value);
        return this;
    }

    protected abstract void doParse(String value);
}
