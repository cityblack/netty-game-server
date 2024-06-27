package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.MethodInvoke;

import java.util.List;

/**
 * Method invoke
 */
public interface InvokeSupport {

    EnhanceMethodInvoke getActionHandler(int msgId);

    boolean containMapping(int msgId);

    void register(int msgId, EnhanceMethodInvoke invoke);

    List<EnhanceMethodInvoke> getAllActionHandler();
}
