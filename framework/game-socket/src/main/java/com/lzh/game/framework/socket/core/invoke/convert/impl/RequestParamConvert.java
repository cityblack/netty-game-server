package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.Convert;
import com.lzh.game.framework.socket.core.protocol.Request;

/**
 * @author zehong.l
 * @since 2024-06-28 12:13
 **/
public class RequestParamConvert implements Convert<Request> {

    @Override
    public Request convert(Request request) {
        return request;
    }

    @Override
    public boolean match(Class<?> type) {
        return type == Request.class;
    }
}
