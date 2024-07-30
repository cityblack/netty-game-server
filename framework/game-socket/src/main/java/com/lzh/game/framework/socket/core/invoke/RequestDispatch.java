package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.protocol.Request;

import java.util.function.Consumer;

public interface RequestDispatch {

    default void handle(Request request) {
        handle(request, null);
    }

    void handle(Request request, Consumer<Object> callBack);
}
