package com.lzh.game.socket.core.filter;

import com.lzh.game.socket.core.ServerExchange;

public interface FilterChain {

    void filter(ServerExchange exchange);

}
