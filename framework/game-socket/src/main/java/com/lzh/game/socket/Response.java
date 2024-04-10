package com.lzh.game.socket;

import com.lzh.game.socket.core.AbstractCommand;

import java.io.Serializable;

public class Response extends AbstractCommand
        implements Serializable {

    private static final long serialVersionUID = 802660945444591938L;

    private Request request;

    private int status;

    private Throwable error;

    public Throwable getError() {
        return error;
    }

    public static Response of(Request request) {
        Response response = new Response();
        response.request = request;
        return response;
    }

    public static Response of(int msgId, int requestId, Object data) {
        Response response = new Response();
        response.setData(data);
        response.setRequestId(requestId);
        response.setMsgId(msgId);
        return response;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
