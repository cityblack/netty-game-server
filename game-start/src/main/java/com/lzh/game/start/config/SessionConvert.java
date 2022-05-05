package com.lzh.game.start.config;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.ParamConvert;
import com.lzh.game.socket.core.session.Session;

public class SessionConvert implements ParamConvert<Session> {

    @Override
    public Session convert(Request request) {
        return request.getSession();
    }
}
