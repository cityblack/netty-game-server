package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.utils.bean.MethodInvoke;

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
