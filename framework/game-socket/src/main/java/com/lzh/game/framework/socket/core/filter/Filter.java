package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.invoke.RequestContext;

public interface Filter {

    void doFilter(RequestContext context, FilterChain chain);
}
