package com.lzh.game.socket.core.invoke;


import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.Request;

public interface InvokeMethodArgumentValues {

    Object[] transfer(Request request, HandlerMethod handlerMethod) throws Exception;
}
