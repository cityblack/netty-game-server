package com.lzh.game.socket.core;

import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.session.Session;

public class ServerExchangeWrapper implements ServerExchange {

    private Request request;
    private Response response;

    public ServerExchangeWrapper(Request request, int responseCommandKey) {
        this.request = request;
        this.response = GameResponse.of(request, responseCommandKey);
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
