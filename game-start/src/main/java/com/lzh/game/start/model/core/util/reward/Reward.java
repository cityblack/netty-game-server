package com.lzh.game.start.model.core.util.reward;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.Verify;

public interface Reward extends Verify {

    default void reward(Object t, LogReason logReason) {
        reward(t, 1, logReason);
    }

    void reward(Object t, int multiple, LogReason logReason);
}
