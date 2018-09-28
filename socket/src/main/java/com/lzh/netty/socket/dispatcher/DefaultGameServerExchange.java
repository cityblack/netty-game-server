package com.lzh.netty.socket.dispatcher;

import com.lzh.netty.socket.protocol.GameResponse;
import com.lzh.netty.socket.protocol.Request;
import com.lzh.netty.socket.protocol.Response;
import com.lzh.netty.socket.protocol.session.Session;

public class DefaultGameServerExchange implements ServerExchange {

    private Request request;
    private Response response;

    public DefaultGameServerExchange(Request request) {
        this.request = request;
        this.response = new GameResponse();
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public Response getResponse() {
        return response;
    }

    @Override
    public Session getSession() {
        if (request == null) {
            return null;
        }
        return request.getSession();
    }
}
