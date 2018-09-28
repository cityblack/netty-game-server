package com.lzh.netty.socket.protocol;

import com.lzh.netty.socket.protocol.coder.AbstractResponse;
import com.lzh.netty.socket.util.JsonUtil;

import java.io.Serializable;

public class GameResponse implements Response,Serializable {

    private static final long serialVersionUID = 802660945444591938L;

    private int protocolId;

    private int code;

    private Object data;

    @Override
    public Object data() {
        return data;
    }

    @Override
    public int codeState() {
        return code;
    }

    @Override
    public int protocolId() {
        return protocolId;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public void setCodeState(int state) {
        this.code = state;
    }


    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    @Override
    public AbstractResponse.Response toProtoResponse() {
        return AbstractResponse.Response.newBuilder()
                .setCode(this.code)
                .setData(JsonUtil.toJSON(this.data))
                .setProtocol(this.protocolId)
                .build();
    }
}
