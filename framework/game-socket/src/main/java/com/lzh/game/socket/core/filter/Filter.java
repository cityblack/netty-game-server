package com.lzh.game.socket.core.filter;

public interface Filter {

    void doFilter(RemoteContext context, FilterChain chain);
}
