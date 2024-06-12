package com.lzh.game.socket.core.invoke;

import java.util.List;

/**
 * Method invoke
 */
public interface InvokeSupport {

    MethodInvoke getActionHandler(int msgId);

    boolean containMapping(int msgId);

    void register(int msgId, MethodInvoke invoke);

    List<MethodInvoke> getAllActionHandler();
}
