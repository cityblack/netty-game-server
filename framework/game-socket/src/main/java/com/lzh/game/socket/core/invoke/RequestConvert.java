package com.lzh.game.socket.core.invoke;

import com.lzh.game.socket.core.protocol.Request;

@FunctionalInterface
public interface RequestConvert<T> {
    T convert(Request request);
}
