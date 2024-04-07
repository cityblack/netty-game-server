package com.lzh.game.socket.core.invoke;

@FunctionalInterface
public interface RequestConvert<T> {
    T convert(Request request);
}
