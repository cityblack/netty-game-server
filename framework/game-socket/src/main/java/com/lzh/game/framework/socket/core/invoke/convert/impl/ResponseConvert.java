package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;

/**
 * @author zehong.l
 * @since 2024-06-28 12:15
 **/
public class ResponseConvert implements RequestConvert<Response> {

    @Override
    public Response convert(Request request) {
        return request.getResponse();
    }

    @Override
    public boolean match(Class<?> type) {
        return type == Response.class;
    }
}
