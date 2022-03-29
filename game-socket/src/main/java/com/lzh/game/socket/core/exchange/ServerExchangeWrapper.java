package com.lzh.game.socket.core.exchange;

import com.lzh.game.socket.core.Request;
import com.lzh.game.socket.core.Response;
import com.lzh.game.socket.core.ServerExchange;
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
