package com.lzh.game.socket.dispatcher.filter;

import com.lzh.game.socket.dispatcher.ServerExchange;

public interface FilterChain {

    void filter(ServerExchange exchange);

}
