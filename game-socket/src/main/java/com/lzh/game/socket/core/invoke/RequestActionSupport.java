package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;

public interface RequestActionSupport<E extends HandlerMethod> extends ActionMethodSupport<E> {

    int getRequestRelation(int requestCmd);

    void register(int requestCmd, E method, int responseCmd);
}
