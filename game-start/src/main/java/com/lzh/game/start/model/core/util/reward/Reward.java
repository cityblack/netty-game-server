package com.lzh.game.start.model.core.util.reward;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.Verify;

public interface Reward<T> extends Verify<T> {

    default void reward(T t, LogReason logReason) {
        reward(t, 1, logReason);
    }

    void reward(T t, int multiple, LogReason logReason);
}
