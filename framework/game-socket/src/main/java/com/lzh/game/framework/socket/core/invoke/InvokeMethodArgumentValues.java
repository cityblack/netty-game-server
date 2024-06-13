package com.lzh.game.framework.socket.core.invoke;


import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.framework.socket.core.protocol.Request;

public interface InvokeMethodArgumentValues {

    Object[] transfer(Request request, HandlerMethod handlerMethod) throws Exception;
}
