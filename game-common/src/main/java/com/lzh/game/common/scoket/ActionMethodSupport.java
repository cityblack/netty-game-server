package com.lzh.game.common.scoket;


import com.lzh.game.common.bean.HandlerMethod;

/**
 * Method invoke
 */
public interface ActionMethodSupport<E extends HandlerMethod> {

    E getActionHandler(int cmd);

    boolean containMapping(int cmd);

    void register(int cmd, E methodMapping);
}
