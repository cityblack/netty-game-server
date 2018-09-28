package com.lzh.netty.socket.dispatcher.filter;

import com.lzh.netty.socket.dispatcher.ServerExchange;
import reactor.core.publisher.Mono;

public interface FilterChain {

    void filter(ServerExchange exchange);

}
