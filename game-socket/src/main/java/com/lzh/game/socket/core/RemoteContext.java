package com.lzh.game.socket.core;

import com.lzh.game.socket.core.session.Session;

public class RemoteContext {

    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
