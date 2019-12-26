package com.lzh.game.socket.exchange.session;

/**
 * Session close listener
 * @param <S>
 */
public interface SessionCloseListener<S> {

    void onSuccess(S s);

    void onFail(S s, Throwable throwable);

    /**
     * Regardless of success or failure will execute the convent
     * @param s
     */
    void onComplete(S s);
}
