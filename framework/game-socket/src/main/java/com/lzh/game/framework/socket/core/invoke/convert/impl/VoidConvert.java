package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.protocol.Request;

/**
 * @author zehong.l
 * @since 2024-07-02 14:48
 **/
public class VoidConvert implements RequestConvert<Object> {

    @Override
    public Object convert(Request request) {
        return null;
    }

    @Override
    public boolean match(Class<?> type) {
        return type == Void.class;
    }
}
