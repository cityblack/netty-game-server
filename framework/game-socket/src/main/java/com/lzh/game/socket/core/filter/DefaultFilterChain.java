package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.protocol.Request;
import com.lzh.game.socket.core.process.RequestDispatch;

import java.util.List;

public class DefaultFilterChain implements FilterChain {

    private List<Filter> filters;

    private RequestDispatch handler;

    private final int index;

    public DefaultFilterChain(List<Filter> filters, RequestDispatch handler) {
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
    public void filter(Request request) {
        if (this.index < this.filters.size()) {
            Filter filter = this.filters.get(index);
            filter.doFilter(request, new DefaultFilterChain(this,index + 1));
        } else {
            handler.handle(request);
        }
    }
}
