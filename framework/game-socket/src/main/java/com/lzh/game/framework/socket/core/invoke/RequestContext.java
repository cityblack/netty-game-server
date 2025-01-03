package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * @author zehong.l
 * @since 2025-01-03 12:13
 **/
@Getter
@Setter
public class RequestContext {

    private Request request;

    private Response response;

    private Consumer<Object> back;

    public RequestContext(Request request, Response response) {
        this.request = request;
        this.response = response;
    }
}
