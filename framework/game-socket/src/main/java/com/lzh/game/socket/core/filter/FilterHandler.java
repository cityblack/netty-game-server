package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestDispatch;

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
