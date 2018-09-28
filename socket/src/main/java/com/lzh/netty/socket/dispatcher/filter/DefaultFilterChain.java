package com.lzh.netty.socket.dispatcher.filter;


import com.lzh.netty.socket.dispatcher.ServerExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class DefaultFilterChain implements FilterChain {

    private List<Filter> filters;
    private final int index;
    private Runnable mono;

    public DefaultFilterChain(List<Filter> filters, Runnable mono) {
        this.filters = filters;
        this.index = 0;
        this.mono = mono;
    }

    public DefaultFilterChain(DefaultFilterChain parent, int index) {
        this.filters = parent.filters;
        this.mono = parent.mono;
        this.index = index;
    }

    @Override
    public void filter(ServerExchange exchange) {
        if (this.index < this.filters.size()) {
            Filter filter = this.filters.get(index);
            filter.doFilter(exchange, new DefaultFilterChain(this,index + 1));
        } else {
            mono.run();
        }
    }
}
