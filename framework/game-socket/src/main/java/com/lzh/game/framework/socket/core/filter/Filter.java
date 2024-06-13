package com.lzh.game.framework.socket.core.filter;

import com.lzh.game.framework.socket.core.protocol.Request;

public interface Filter {

    void doFilter(Request request, FilterChain chain);
}
