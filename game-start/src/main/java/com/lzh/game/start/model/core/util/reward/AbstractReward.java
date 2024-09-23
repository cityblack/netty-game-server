package com.lzh.game.start.model.core.util.reward;

import com.lzh.game.start.model.core.util.AbstractVerify;

public abstract class AbstractReward extends AbstractVerify implements Reward {

    public final AbstractReward parse(String value) {
        doParse(value);
        return this;
    }

    protected abstract void doParse(String value);
}
