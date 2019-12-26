package com.lzh.game.socket.dispatcher.action;

import com.lzh.game.socket.dispatcher.mapping.RequestMethodMapping;

/**
 * Invoke {@link com.lzh.game.socket.annotation.RequestMapping} mapping convent
 */
public interface ActionMethodSupport {

    RequestMethodMapping getActionHandler(int cmd);

    boolean containMapping(int cmd);

    void registerCmd(int cmd, RequestMethodMapping methodMapping);
}
