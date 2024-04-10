package com.lzh.game.socket.core.invoke.support;

import com.lzh.game.socket.core.protocol.Request;
import com.lzh.game.socket.core.protocol.Response;

@FunctionalInterface
public interface ErrorHandler {

    boolean resolveException(Exception ex, Request request, Response response);
}
