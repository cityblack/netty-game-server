package com.lzh.game.start.filter;

import com.lzh.game.socket.autoconfig.GameSocketProperties;
import com.lzh.game.socket.dispatcher.ServerExchange;
import com.lzh.game.socket.dispatcher.filter.Filter;
import com.lzh.game.socket.dispatcher.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProtocolVersionFilter implements Filter {

    @Autowired
    private GameSocketProperties gameSocketProperties;

    @Override
    public void doFilter(ServerExchange exchange, FilterChain chain) {
        chain.filter(exchange);
        /*if (gameProperties.getProtocolVersion().equals(exchange.getRequest().header().getVersion())) {
            chain.filter(exchange);
        } else {
            if (log.isInfoEnabled()) {
                log.info("No valid exchange version request, session {}", exchange.getSession().cacheKey());
            }
        }*/
    }
}
