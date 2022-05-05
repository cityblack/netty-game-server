package com.lzh.game.socket.core.invoke.support;

import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.GameResponse;

@FunctionalInterface
public interface ErrorHandler {

    boolean resolveException(Exception ex, GameRequest request, GameResponse response);
}
