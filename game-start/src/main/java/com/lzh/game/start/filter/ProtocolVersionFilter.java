package com.lzh.game.start.filter;

import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProtocolVersionFilter implements Filter {

    @Autowired
    private GameServerSocketProperties gameSocketProperties;

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
