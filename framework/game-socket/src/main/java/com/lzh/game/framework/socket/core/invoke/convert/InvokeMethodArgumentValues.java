package com.lzh.game.framework.socket.core.invoke.convert;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.common.method.EnhanceMethodInvoke;

public interface InvokeMethodArgumentValues {

    Object[] transfer(Request request, EnhanceMethodInvoke handlerMethod);
}
