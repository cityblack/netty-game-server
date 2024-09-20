package com.lzh.game.framework.socket.core.invoke.convert;

import com.lzh.game.framework.socket.core.protocol.Request;

public interface Convert<T> {
    T convert(Request request);

    boolean match(Class<?> type);
}
