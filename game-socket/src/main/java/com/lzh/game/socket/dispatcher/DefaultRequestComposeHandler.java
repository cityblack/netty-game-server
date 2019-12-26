package com.lzh.game.socket.dispatcher;

import com.lzh.game.common.OptionListener;
import com.lzh.game.socket.dispatcher.action.ActionCenter;
import com.lzh.game.socket.dispatcher.filter.*;
import com.lzh.game.socket.exchange.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class DefaultRequestComposeHandler implements RequestComposeHandler, ApplicationContextAware {

    private FilterConfigManage configManage;

    @Override
    public void handler(ServerExchange exchange, ActionCenter actionCenter, OptionListener<ServerExchange> listener) {

        if (configManage != null && !isAllowAnonRequest(configManage.getConfig(),exchange.getRequest())) {
            FilterChain chain = new DefaultFilterChain(configManage.getConfig().getFilters()
                    , () -> actionCenter.executeAction(exchange, listener));
            chain.filter(exchange);
        } else {
            actionCenter.executeAction(exchange, listener);
        }
    }


    private boolean isAllowAnonRequest(FilterConfig config, Request request) {

        return config.getAnons().stream().anyMatch(e -> e.equals(request.header().getCmd()));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            configManage = applicationContext.getBean(FilterConfigManage.class);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Not enable filter because of not config filter.");
            }
        }
    }

}
