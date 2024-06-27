package com.lzh.game.framework.socket.core.invoke.convert;

import com.lzh.game.framework.socket.core.protocol.Request;

@FunctionalInterface
public interface RequestConvert<T> {
    T convert(Request request);
}
