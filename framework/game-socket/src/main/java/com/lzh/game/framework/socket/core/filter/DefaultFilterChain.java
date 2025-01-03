package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.invoke.RequestContext;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;

import java.util.List;

public class DefaultFilterChain implements FilterChain {

    private final List<Filter> filters;

    private final RequestDispatch handler;

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
    public void filter(RequestContext context) {
        if (this.index < this.filters.size()) {
            Filter filter = this.filters.get(index);
            filter.doFilter(context, new DefaultFilterChain(this, index + 1));
        } else {
            handler.handle(context);
        }
    }
}
