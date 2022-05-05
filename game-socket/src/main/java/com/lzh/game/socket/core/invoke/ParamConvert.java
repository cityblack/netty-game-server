package com.lzh.game.socket.core.invoke;

import com.lzh.game.socket.Request;

@FunctionalInterface
public interface ParamConvert<T> {

    T convert(Request request);

}
