package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.ServerExchange;

import java.util.List;

public class FilterHandler implements RequestHandler {

    private final DefaultFilterChain chain;

    public FilterHandler(List<Filter> filters, RequestHandler handler) {
        this.chain = new DefaultFilterChain(filters, handler);
    }

    @Override
    public void handler(ServerExchange exchange) {
        this.chain.filter(exchange);
    }
}
