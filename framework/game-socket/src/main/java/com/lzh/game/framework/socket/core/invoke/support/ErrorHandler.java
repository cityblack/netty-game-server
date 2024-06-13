package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;

@FunctionalInterface
public interface ErrorHandler {

    boolean resolveException(Exception ex, Request request, Response response);
}
