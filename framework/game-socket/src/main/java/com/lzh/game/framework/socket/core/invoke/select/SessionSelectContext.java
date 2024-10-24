package com.lzh.game.framework.socket.core.invoke.select;

import com.lzh.game.framework.socket.core.protocol.Request;

/**
 * @author zehong.l
 * @since 2024-10-24 11:09
 **/
public class SessionSelectContext {

    private Request request;

    public SessionSelectContext(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
