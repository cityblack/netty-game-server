package com.lzh.game.framework.logs.invoke;

/**
 * @author zehong.l
 * @since 2024-07-10 10:54
 **/
public interface LogInvoke {

    void invoke(LogInvokeInfo invokeInfo, Object[] args);
}
