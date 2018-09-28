package com.lzh.netty.start.filter;

import com.lzh.netty.socket.autoconfig.GameProperties;
import com.lzh.netty.socket.dispatcher.ServerExchange;
import com.lzh.netty.socket.dispatcher.filter.Filter;
import com.lzh.netty.socket.dispatcher.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ProtocolVersionFilter implements Filter {
    @Autowired
    private GameProperties gameProperties;

    @Override
    public void doFilter(ServerExchange exchange, FilterChain chain) {

        if (gameProperties.getProtocolVersion().equals(exchange.getRequest().version())) {
            chain.filter(exchange);
        } else {
            if (log.isInfoEnabled()) {
                log.info("No valid protocol version request, session {}", exchange.getSession().getId());
            }
        }
    }
}
