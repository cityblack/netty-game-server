package com.lzh.game.socket.core;

import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.core.session.Session;

public class RemoteContext {

    private Session session;

    private GameRequest request;

    private GameResponse response;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public GameRequest getRequest() {
        return request;
    }

    public void setRequest(GameRequest request) {
        this.request = request;
    }

    public GameResponse getResponse() {
        return response;
    }

    public void setResponse(GameResponse response) {
        this.response = response;
    }
}
