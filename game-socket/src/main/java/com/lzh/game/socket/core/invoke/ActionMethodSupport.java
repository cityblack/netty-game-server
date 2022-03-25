package com.lzh.game.socket.core.invoke;


import com.lzh.game.common.bean.HandlerMethod;

/**
 * Invoke {@link com.lzh.game.socket.annotation.RequestMapping} mapping convent
 */
public interface ActionMethodSupport<E extends HandlerMethod> {

    E getActionHandler(int cmd);

    boolean containMapping(int cmd);

    void registerCmd(int cmd, E methodMapping);

    void registerRequestRelation(int requestCmd, int responseCmd);

    int getRequestRelation(int requestCmd);
}
