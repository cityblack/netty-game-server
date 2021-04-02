package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.ServerExchange;

public interface Filter {

    void doFilter(ServerExchange exchange, FilterChain chain);
}
