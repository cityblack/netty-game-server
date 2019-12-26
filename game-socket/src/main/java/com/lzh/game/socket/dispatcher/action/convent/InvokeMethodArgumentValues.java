package com.lzh.game.socket.dispatcher.action.convent;


import com.lzh.game.common.bean.HandlerMethod;

public interface InvokeMethodArgumentValues<T> {

    Object[] transfer(T value, HandlerMethod handlerMethod) throws Exception;
}
