package com.lzh.game.start.filter;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProtocolVersionFilter implements Filter {

    @Autowired
    private GameServerSocketProperties gameSocketProperties;

    @Override
    public void doFilter(RemoteContext context, FilterChain chain) {
        chain.filter(context);
        /*if (gameProperties.getProtocolVersion().equals(exchange.getRequest().header().getVersion())) {
            chain.filter(exchange);
        } else {
            if (log.isInfoEnabled()) {
                log.info("No valid exchange version request, session {}", exchange.getSession().cacheKey());
            }
        }*/
    }
}
