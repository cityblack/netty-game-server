package com.lzh.game.socket;

import com.lzh.game.socket.core.session.Session;

import java.io.Serializable;

public class GameRequest implements Request, Serializable {

    private static final long serialVersionUID = 1550620526955432911L;

    private int cmd;

    private int version;

    private Object data;

    private byte[] bytes;

    private long requestId;

    private transient Session session;

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Object data() {
        return this.data;
    }

    @Override
    public byte[] byteData() {
        return this.bytes;
    }

    @Override
    public int cmd() {
        return cmd;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public long requestId() {
        return requestId;
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

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}
