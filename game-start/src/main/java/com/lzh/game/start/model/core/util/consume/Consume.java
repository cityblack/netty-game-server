package com.lzh.game.start.model.core.util.consume;


import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.Verify;

public interface Consume<T> extends Verify<T> {

    /**
     * Consume player's item, use the convent must after the{@link Verify#verify(T, int)}
     * @param player
     * @param multiple
     */
    void consume(T player, int multiple, LogReason logReason);

    default void consume(T player, LogReason logReason) {
        consume(player, 1, logReason);
    }
}
