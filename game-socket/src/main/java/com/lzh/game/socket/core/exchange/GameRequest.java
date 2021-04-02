package com.lzh.game.socket.core.exchange;

import com.lzh.game.socket.core.Request;
import com.lzh.game.socket.core.session.Session;

public class GameRequest implements Request<byte[]> {

    private static final long serialVersionUID = 1550620526955432911L;

    private int cmd;

    private int version;

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
    public int getCmd() {
        return cmd;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int getPort() {
        return session.getPort();
    }

    @Override
    public String getRemoteAddress() {
        return session.getRemoteAddress();
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static GameRequest of(int cmd, int version, byte[] data, Session session) {
        GameRequest request = new GameRequest();
        request.cmd = cmd;
        request.version = version;
        request.data = data;
        request.session = session;
        return request;
    }
}
