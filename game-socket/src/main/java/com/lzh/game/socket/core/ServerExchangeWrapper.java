package com.lzh.game.socket.core;

import com.lzh.game.common.scoket.Request;
import com.lzh.game.common.scoket.Response;
import com.lzh.game.common.scoket.GameResponse;
import com.lzh.game.common.scoket.session.Session;

public class ServerExchangeWrapper implements ServerExchange {

    private Request request;
    private Response response;

    public ServerExchangeWrapper(Request request) {
        this.request = request;
        this.response = GameResponse.of(request);
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
