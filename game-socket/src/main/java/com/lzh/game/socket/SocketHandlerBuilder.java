package com.lzh.game.socket;

import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.socket.core.*;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.common.scoket.session.Session;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SocketHandlerBuilder {

    public static final String REQUEST_HANDLER_BEAN = "requestHandler";

    public static final String BUSINESS_POOL = "requestBusinessPool";

    private RequestBusinessPool requestBusinessPool;

    private RequestHandler requestHandler;

    private final ApplicationContext applicationContext;

    private final List<Filter> filters = new ArrayList<>();

    private SocketHandlerBuilder(RequestHandler requestHandler, ApplicationContext applicationContext) {
        this.requestHandler = requestHandler;
        this.applicationContext = applicationContext;
    }

    /**
     * Load default
     * @param context
     * @return
     */
    public static SocketHandlerBuilder application(ApplicationContext context) {
        SocketHandlerBuilder builder = new SocketHandlerBuilder(
                context.getBean(REQUEST_HANDLER_BEAN, RequestHandler.class), context);
        List<Filter> filters = context
                .getBeanProvider(Filter.class)
                .orderedStream()
                .collect(Collectors.toList());
        builder.filters.addAll(filters);
        builder.requestBusinessPool = loadPool(context);
        return builder;
    }

    private static RequestBusinessPool loadPool(ApplicationContext context) {
        RequestBusinessPool pool = context.getBean(BUSINESS_POOL, RequestBusinessPool.class);
        if (Objects.isNull(pool)) {
            return new InnerRequestBusinessPool();
        }
        return pool;
    }

    public MessageHandler build() {
        FilterHandler handler = new FilterHandler(this.filters, this.requestHandler);
        MessageHandler messageHandler = new MessageHandlerImpl(handler, this.requestBusinessPool);
        return messageHandler;
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
