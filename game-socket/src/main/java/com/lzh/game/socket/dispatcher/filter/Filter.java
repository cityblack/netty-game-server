package com.lzh.game.socket.dispatcher.filter;

import com.lzh.game.socket.dispatcher.ServerExchange;

public interface Filter {

    void doFilter(ServerExchange exchange, FilterChain chain);
}
