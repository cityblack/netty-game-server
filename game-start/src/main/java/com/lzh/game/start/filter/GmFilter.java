package com.lzh.game.start.filter;

import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterChain;
import com.lzh.game.start.cmd.impl.CmdMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GmFilter implements Filter {

    @Autowired
    private GameServerSocketProperties properties;

    private static final int DEFAULT_GM_PROTOCOL = CmdMessage.CM_GM;

    @Override
    public void doFilter(Request request, FilterChain chain) {

        if (DEFAULT_GM_PROTOCOL == context.getRequest().cmd()) {
            if (!properties.isOpenGm()) {
                if (log.isInfoEnabled()) {
                    log.info("Gm server is not open, please check the request is valid [{}]"
                            , context.getSession().getRemoteAddress());
                }
                return;
            }
        }

        chain.filter(context);
    }
}
