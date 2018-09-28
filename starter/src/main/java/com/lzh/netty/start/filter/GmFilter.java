package com.lzh.netty.start.filter;

import com.lzh.netty.socket.autoconfig.GameProperties;
import com.lzh.netty.socket.dispatcher.ServerExchange;
import com.lzh.netty.socket.dispatcher.filter.Filter;
import com.lzh.netty.socket.dispatcher.filter.FilterChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GmFilter implements Filter {
    @Autowired
    private GameProperties properties;

    private static final int DEFAULT_GM_PROTOCOL = 1;

    @Override
    public void doFilter(ServerExchange exchange, FilterChain chain) {

        if (DEFAULT_GM_PROTOCOL == exchange.getRequest().getProtocolId()) {
            if (!properties.isOpenGm()) {
                if (log.isInfoEnabled()) {
                    log.info("Gm server is not open, please check the request is valid {}"
                            , exchange.getSession().getRemoteAddress());
                }
                return;
            }
        }

        chain.filter(exchange);
    }
}
