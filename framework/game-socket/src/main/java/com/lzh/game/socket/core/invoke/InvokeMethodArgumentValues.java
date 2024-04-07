package com.lzh.game.socket.core.invoke;


import com.lzh.game.common.bean.HandlerMethod;

public interface InvokeMethodArgumentValues {

    Object[] transfer(Request request, HandlerMethod handlerMethod) throws Exception;
}
