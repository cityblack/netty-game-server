package com.lzh.game.framework.socket.core.invoke.convert.impl;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * @author zehong.l
 * @since 2024-06-28 12:14
 **/
public class SessionConvert implements RequestConvert<Session> {

    @Override
    public Session convert(Request request) {
        return request.getSession();
    }

    @Override
    public boolean match(Class<?> type) {
        return Session.class.isAssignableFrom(type);
    }
}
