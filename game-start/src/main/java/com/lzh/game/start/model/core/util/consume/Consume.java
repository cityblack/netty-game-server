package com.lzh.game.start.model.core.util.consume;


import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.Verify;

public interface Consume extends Verify {

    /**
     * Consume player's item, use the convent must after the{@link Verify#verify(Object, int)}
     * @param player
     * @param multiple
     */
    void consume(Object player, int multiple, LogReason logReason);

    default void consume(Object player, LogReason logReason) {
        consume(player, 1, logReason);
    }
}
