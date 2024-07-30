package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;

import java.util.List;
import java.util.function.Consumer;

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
    public void filter(Request request, Consumer<Object> callBack) {
        if (this.index < this.filters.size()) {
            Filter filter = this.filters.get(index);
            filter.doFilter(request, new DefaultFilterChain(this, index + 1));
        } else {
            handler.handle(request, callBack);
        }
    }
}
