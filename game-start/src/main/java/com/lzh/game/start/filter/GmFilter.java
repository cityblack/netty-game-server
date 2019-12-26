package com.lzh.game.start.filter;

import com.lzh.game.socket.autoconfig.GameSocketProperties;
import com.lzh.game.socket.dispatcher.ServerExchange;
import com.lzh.game.socket.dispatcher.filter.Filter;
import com.lzh.game.socket.dispatcher.filter.FilterChain;
import com.lzh.game.start.cmd.CmdMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GmFilter implements Filter {

    @Autowired
    private GameSocketProperties properties;

    private static final int DEFAULT_GM_PROTOCOL = CmdMessage.CM_GM;

    @Override
    public void doFilter(ServerExchange exchange, FilterChain chain) {

        if (DEFAULT_GM_PROTOCOL == exchange.getRequest().header().getCmd()) {
            if (!properties.isOpenGm()) {
                if (log.isInfoEnabled()) {
                    log.info("Gm server is not open, please check the request is valid [{}]"
                            , exchange.getSession().getRemoteAddress());
                }
                return;
            }
        }

        chain.filter(exchange);
    }
}
