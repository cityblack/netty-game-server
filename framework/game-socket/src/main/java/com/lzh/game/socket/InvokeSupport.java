package com.lzh.game.socket;


import com.lzh.game.common.bean.HandlerMethod;

import java.util.List;

/**
 * Method invoke
 */
public interface InvokeSupport<E extends HandlerMethod> {

    E getActionHandler(int msgId);

    boolean containMapping(int msgId);

    void register(int msgId, E methodMapping);

    List<E> getAllActionHandler();
}
