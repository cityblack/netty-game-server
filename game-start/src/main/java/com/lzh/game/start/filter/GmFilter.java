package com.lzh.game.start.filter;

import com.lzh.game.framework.socket.core.bootstrap.server.ServerSocketProperties;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterChain;
import com.lzh.game.framework.socket.core.invoke.RequestContext;
import com.lzh.game.start.util.Constant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GmFilter implements Filter {

    @Resource
    private ServerSocketProperties properties;

    @Override
    public void doFilter(RequestContext context, FilterChain chain) {
        var request = context.getRequest();
        if (request.getMsgId() == Constant.GM_MSG_ID && !properties.isOpenGm()) {
            log.error("Gm server is not open, please check the request is valid [{}-{}]"
                    , request.getSession().getId(), request.getSession().getRemoteAddressStr());
            return;
        }

        chain.filter(context);
    }
}
