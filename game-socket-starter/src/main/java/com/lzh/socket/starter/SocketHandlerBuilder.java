package com.lzh.socket.starter;

import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.socket.core.MessageHandlerImpl;
import com.lzh.game.socket.core.RequestBusinessPool;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SocketHandlerBuilder implements ApplicationContextAware {

    public static final String REQUEST_HANDLER_BEAN = "requestHandler";

    public static final String BUSINESS_POOL = "requestBusinessPool";

    private MessageHandler handler;

    /**
     * Load default
     * @param context
     * @return
     */
    public MessageHandler init(ApplicationContext context) {
        RequestHandler handler = context.getBean(REQUEST_HANDLER_BEAN, RequestHandler.class);
        List<Filter> filters = context
                .getBeanProvider(Filter.class)
                .orderedStream()
                .collect(Collectors.toList());
        RequestBusinessPool pool = loadPool(context);
        FilterHandler wrapper = new FilterHandler(filters, handler);
        MessageHandler messageHandler = new MessageHandlerImpl(wrapper, pool);
        return messageHandler;
    }

    private static RequestBusinessPool loadPool(ApplicationContext context) {
        try {
            return context.getBean(BUSINESS_POOL, RequestBusinessPool.class);
        } catch (NoSuchBeanDefinitionException e) {
            return new InnerRequestBusinessPool();
        }
    }

    public MessageHandler getHandler() {
        return handler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.handler = init(applicationContext);
    }

    private static class InnerRequestBusinessPool implements RequestBusinessPool {

        @Override
        public void submit(ServerExchange exchange, Runnable runnable) {
            CompletableFuture.runAsync(runnable);
        }

        @Override
        public void submit(Session session, Runnable runnable) {
            CompletableFuture.runAsync(runnable);
        }
    }
}
