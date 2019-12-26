package com.lzh.game.socket.exchange.request;

import com.lzh.game.socket.exchange.Request;
import com.lzh.game.socket.exchange.session.Session;

public class GameRequest implements Request<byte[]> {

    private static final long serialVersionUID = 1550620526955432911L;

    private RequestHeader header;

    private byte[] data;

    private transient Session session;


    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public byte[] data() {
        return this.data;
    }

    @Override
    public int port() {
        return session.getPort();
    }

    @Override
    public String remoteAddress() {
        return session.getRemoteAddress();
    }

    @Override
    public RequestHeader header() {
        return header;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }
}
