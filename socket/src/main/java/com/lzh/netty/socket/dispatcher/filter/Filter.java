package com.lzh.netty.socket.dispatcher.filter;

import com.lzh.netty.socket.dispatcher.ServerExchange;

public interface Filter {

    void doFilter(ServerExchange exchange, FilterChain chain);
}
