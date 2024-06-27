package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;

import java.util.List;

public class FilterHandler implements RequestDispatch {

    private final DefaultFilterChain chain;

    public FilterHandler(List<Filter> filters, RequestDispatch handler) {
        this.chain = new DefaultFilterChain(filters, handler);
    }

    @Override
    public void handle(Request request) {
        this.chain.filter(request);
    }
}
