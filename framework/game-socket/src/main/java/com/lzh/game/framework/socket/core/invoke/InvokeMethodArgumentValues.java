package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.HandlerMethod;

public interface InvokeMethodArgumentValues {

    Object[] transfer(Request request, EnhanceMethodInvoke handlerMethod) throws Exception;
}
