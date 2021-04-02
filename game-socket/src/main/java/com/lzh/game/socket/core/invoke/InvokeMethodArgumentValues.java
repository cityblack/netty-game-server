package com.lzh.game.socket.core.invoke;


import com.lzh.game.common.bean.HandlerMethod;

public interface InvokeMethodArgumentValues<T> {

    Object[] transfer(T value, HandlerMethod handlerMethod) throws Exception;
}
