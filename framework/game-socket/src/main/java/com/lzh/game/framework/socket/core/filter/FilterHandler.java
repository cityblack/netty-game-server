package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;

import java.util.List;
import java.util.function.Consumer;

public class FilterHandler implements RequestDispatch {

    private final DefaultFilterChain chain;

    public FilterHandler(List<Filter> filters, RequestDispatch handler) {
        this.chain = new DefaultFilterChain(filters, handler);
    }

    @Override
    public void handle(Request request, Consumer<Object> callBack) {
        this.chain.filter(request, callBack);
    }
}
