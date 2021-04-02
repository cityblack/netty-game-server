package com.lzh.game.socket.core.invoke;


/**
 * Invoke {@link com.lzh.game.socket.annotation.RequestMapping} mapping convent
 */
public interface ActionMethodSupport {

    RequestMethodMapping getActionHandler(int cmd);

    boolean containMapping(int cmd);

    void registerCmd(int cmd, RequestMethodMapping methodMapping);
}
