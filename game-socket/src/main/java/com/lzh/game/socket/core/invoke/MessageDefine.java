package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;

/**
 * @author zehong.l
 * @date 2023-07-31 14:34
 **/
public class MessageDefine {
    // proto id
    private int id;
    // invoke handler
    private EnhanceHandlerMethod method;

    private ParamConvert<?> convert;

    private int protocolType;
//    private
}
