package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.RemoteContext;

public interface Filter {

    void doFilter(RemoteContext context, FilterChain chain);
}
