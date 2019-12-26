package com.lzh.game.socket.dispatcher;

import com.lzh.game.socket.exchange.response.GameResponse;
import com.lzh.game.socket.exchange.Request;
import com.lzh.game.socket.exchange.Response;
import com.lzh.game.socket.exchange.session.Session;

public class ServerExchangeWrapper implements ServerExchange {

    private Request request;
    private Response response;

    public ServerExchangeWrapper(Request request) {
        this.request = request;
        this.response = GameResponse.of();
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
