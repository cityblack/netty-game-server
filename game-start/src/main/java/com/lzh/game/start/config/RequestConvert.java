package com.lzh.game.start.config;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.ParamConvert;

public class RequestConvert implements ParamConvert<Request> {

    @Override
    public Request convert(Request request) {
        return request;
    }
}
