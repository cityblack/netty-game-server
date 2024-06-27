package com.lzh.game.framework.socket.core.invoke.convert;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;

public interface InvokeMethodArgumentValues {

    Object[] transfer(Request request, EnhanceMethodInvoke handlerMethod);
}
