package com.lzh.game.socket.exchange.response;

import com.lzh.game.socket.exchange.Response;

import java.io.Serializable;

public class GameResponse implements Response,Serializable {

    private static final long serialVersionUID = 802660945444591938L;

    private int protocolId;

    private Object data;

    @Override
    public Object data() {
        return data;
    }

    @Override
    public int cmd() {
        return protocolId;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setCmd(int protocolId) {
        this.protocolId = protocolId;
    }

    public static GameResponse of() {
        return new GameResponse();
    }

    public static GameResponse of(int protocolId, Object data) {
        GameResponse response = new GameResponse();
        response.protocolId = protocolId;
        response.data = data;
        return response;
    }
}
