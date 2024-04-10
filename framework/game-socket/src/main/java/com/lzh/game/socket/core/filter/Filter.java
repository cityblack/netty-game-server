package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.protocol.Request;

public interface Filter {

    void doFilter(Request request, FilterChain chain);
}
