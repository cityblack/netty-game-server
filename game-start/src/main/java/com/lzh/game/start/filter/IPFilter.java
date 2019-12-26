package com.lzh.game.start.filter;

import com.lzh.game.socket.dispatcher.ServerExchange;
import com.lzh.game.socket.dispatcher.filter.Filter;
import com.lzh.game.socket.dispatcher.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class IPFilter implements Filter {
    private List<String> ipArr = Arrays.asList("192.168.0.1");

    @Override
    public void doFilter(ServerExchange exchange, FilterChain chain) {
        if (ipArr.contains(exchange.getSession().getRemoteAddress())) {
            
        } else {
            chain.filter(exchange);
        }
    }
}
