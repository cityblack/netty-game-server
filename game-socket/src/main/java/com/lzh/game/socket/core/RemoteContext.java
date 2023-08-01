package com.lzh.game.socket.core;

import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.session.Session;

public class RemoteContext {

    private Session session;

    private RemotingCommand request;

    private RemotingCommand response;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public RemotingCommand getRequest() {
        return request;
    }

    public void setRequest(RemotingCommand request) {
        this.request = request;
    }

    public RemotingCommand getResponse() {
        return response;
    }

    public void setResponse(RemotingCommand response) {
        this.response = response;
    }
}
