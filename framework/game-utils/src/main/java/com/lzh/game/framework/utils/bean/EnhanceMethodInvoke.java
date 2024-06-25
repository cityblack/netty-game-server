package com.lzh.game.framework.utils.bean;

/**
 * @author zehong.l
 * @since 2024/6/21 22:40
 **/
public interface EnhanceMethodInvoke extends MethodInvoke {

    Class<?>[] getParamsType();

    Class<?> getReturnType();
}
