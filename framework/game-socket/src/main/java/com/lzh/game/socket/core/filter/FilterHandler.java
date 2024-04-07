package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.RequestHandle;

import java.util.List;

public class FilterHandler implements RequestHandle {

    private final DefaultFilterChain chain;

    public FilterHandler(List<Filter> filters, RequestHandle handler) {
        this.chain = new DefaultFilterChain(filters, handler);
    }

    @Override
    public void handle(RemoteContext context) {
        this.chain.filter(context);
    }
}
