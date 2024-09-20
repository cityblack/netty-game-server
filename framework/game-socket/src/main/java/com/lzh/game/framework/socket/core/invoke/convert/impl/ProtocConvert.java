package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.Convert;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;

/**
 * @author zehong.l
 * @since 2024-06-28 12:16
 **/
public class ProtocConvert implements Convert<Object> {

    @Override
    public Object convert(Request request) {
        return request.getData();
    }

    @Override
    public boolean match(Class<?> type) {
        return type.isAnnotationPresent(Protocol.class);
    }
}
