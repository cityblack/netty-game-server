package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.protocol.Request;

@FunctionalInterface
public interface RequestConvert<T> {
    T convert(Request request);
}
