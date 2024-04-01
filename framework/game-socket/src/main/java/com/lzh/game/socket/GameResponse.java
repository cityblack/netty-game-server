package com.lzh.game.socket;

import com.lzh.game.common.util.Constant;

import java.io.Serializable;

public class GameResponse extends AbstractRemotingCommand
        implements Response, Serializable {

    private static final long serialVersionUID = 802660945444591938L;

    private Request request;

    private int status;

    private Throwable error;

    @Override
    public int status() {
        return status;
    }

    public Request getRequest() {
        return request;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
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
        response.setCmd(request.cmd());
        response.setRemoteId(request.remoteId());
        response.setSession(request.getSession());
        response.setType(Constant.RESPONSE_SIGN);
        return response;
    }
}
