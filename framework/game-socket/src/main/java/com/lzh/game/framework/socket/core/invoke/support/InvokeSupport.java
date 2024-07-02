package com.lzh.game.framework.socket.core.invoke.support;

import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.MethodInvoke;

import java.util.List;

/**
 * Method invoke
 */
public interface InvokeSupport {

    EnhanceMethodInvoke getActionHandler(short msgId);

    boolean containMapping(short msgId);

    void register(short msgId, EnhanceMethodInvoke invoke);

    List<EnhanceMethodInvoke> getAllActionHandler();
}
