package com.lzh.netty.socket.protocol;


import com.lzh.netty.socket.protocol.session.Session;

import java.io.Serializable;

public class GameRequestEntity implements Request,GameRequest, Serializable {

    private static final long serialVersionUID = 1550620526955432911L;

    private int protocolId;

    private String data;

    private Session session;

    private Integer version;

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public String getParam(String paramName) {
        return null;
    }

    @Override
    public int getProtocolId() {
        return this.protocolId;
    }

    @Override
    public String data() {
        return this.data;
    }

    @Override
    public Integer version() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public void setDataString(String dataString) {
        this.data = dataString;
    }

}
