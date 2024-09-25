package com.lzh.game.framework.common.method;

/**
 * Java reflect enhance
 * @author zehong.l
 * @since 2024/6/21 21:58
 **/
public interface MethodInvoke {

    /**
     * Execute method
     * @param args method params
     * @return method result
     */
    Object invoke(Object... args);
}
