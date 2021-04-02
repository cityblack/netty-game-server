package com.lzh.game.socket.core.exchange;

import com.lzh.game.socket.core.Request;
import com.lzh.game.socket.core.Response;

import java.io.Serializable;

public class GameResponse implements Response, Serializable {

    private static final long serialVersionUID = 802660945444591938L;

    private Request request;

    private int protocolId;

    private Object data;

    private int status;

    private Throwable error;

    @Override
    public Object data() {
        return data;
    }

    @Override
    public int cmd() {
        return protocolId;
    }

    @Override
    public int status() {
        return status;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setCmd(int protocolId) {
        this.protocolId = protocolId;
    }

    public Request getRequest() {
        return request;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.status = FAIL;
        this.error = error;
    }

    public static GameResponse of(Request request) {
        GameResponse response = new GameResponse();
        response.request = request;
        response.setStatus(OK);
        return response;
    }
}
