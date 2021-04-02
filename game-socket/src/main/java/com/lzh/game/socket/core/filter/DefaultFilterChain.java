package com.lzh.game.socket.core.filter;


import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.ServerExchange;

import java.util.List;

public class DefaultFilterChain implements FilterChain {

    private List<Filter> filters;

    private RequestHandler handler;

    private final int index;

    public DefaultFilterChain(List<Filter> filters, RequestHandler handler) {
        this.filters = filters;
        this.handler = handler;
        this.index = 0;
    }

    private DefaultFilterChain(DefaultFilterChain parent, int index) {
        this.filters = parent.filters;
        this.handler = parent.handler;
        this.index = index;
    }

    @Override
    public void filter(ServerExchange exchange) {
        if (this.index < this.filters.size()) {
            Filter filter = this.filters.get(index);
            filter.doFilter(exchange, new DefaultFilterChain(this,index + 1));
        } else {
            handler.handler(exchange);
        }
    }
}
