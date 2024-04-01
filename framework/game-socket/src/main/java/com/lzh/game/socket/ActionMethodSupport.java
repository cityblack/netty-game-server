package com.lzh.game.socket;


import com.lzh.game.common.bean.HandlerMethod;

import java.util.List;

/**
 * Method invoke
 */
public interface ActionMethodSupport<E extends HandlerMethod> {

    E getActionHandler(int cmd);

    boolean containMapping(int cmd);

    void register(int cmd, E methodMapping);

    List<E> getAllActionHandler();
}
