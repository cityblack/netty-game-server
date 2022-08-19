package com.lzh.game.socket;

import com.lzh.game.socket.core.session.Session;

/**
 * Thread Unsafe
 */
public abstract class AbstractRemotingCommand
        implements RemotingCommand {

    private int cmd;

    private Object data;

    private byte[] bytes;

    private int remoteId;

    private byte type;

    private transient Session session;

    @Override
    public int cmd() {
        return cmd;
    }

    @Override
    public byte[] byteData() {
        return bytes;
    }

    @Override
    public Object data() {
        return data;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public byte type() {
        return type;
    }

    @Override
    public int remoteId() {
        return remoteId;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
