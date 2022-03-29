package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.scoket.ActionMethodSupport;

public interface RequestActionSupport<E extends HandlerMethod> extends ActionMethodSupport<E> {

    void registerRequestRelation(int requestCmd, int responseCmd);

    int getRequestRelation(int requestCmd);
}
