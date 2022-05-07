package com.lzh.game.socket.core.process;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.*;
import com.lzh.game.socket.core.invoke.ConvertManager;
import com.lzh.game.socket.core.invoke.ParamConvert;
import com.lzh.game.socket.core.invoke.RequestActionSupport;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parse target Object data earlier
 */
@Slf4j
public class RequestProcess2 implements Process<GameRequest> {

    private RequestHandler requestHandler;

    private RequestProcessPool pool;

    private ConvertManager convertManager;

    private RequestActionSupport<EnhanceHandlerMethod> support;

    private Map<Integer, ParamConvert<?>> targetObjectConvert = new ConcurrentHashMap<>();

    public RequestProcess2(RequestHandler requestHandler, ConvertManager convertManager, RequestActionSupport<EnhanceHandlerMethod> support) {
        this(requestHandler, new DefaultPool(), convertManager, support);
    }

    public RequestProcess2(RequestHandler requestHandler, RequestProcessPool pool
            , ConvertManager convertManager, RequestActionSupport<EnhanceHandlerMethod> support) {
        this.requestHandler = requestHandler;
        this.pool = pool;
        this.convertManager = convertManager;
        this.support = support;
    }

    @Override
    public void process(RemoteContext context, GameRequest request) {
        ServerExchange exchange = new ServerExchangeWrapper(request, Constant.REQUEST_COMMAND_KEY);
        request.setSession(context.getSession());
        //
        int cmd = request.cmd();
        if (!support.containMapping(cmd)) {
            log.warn("Not find define cmd:{}", cmd);
            return;
        }
        Object value = getConvert(request.cmd()).convert(request);
        request.setData(value);
        pool.submit(exchange, new ProcessTask(exchange));
    }

    private ParamConvert<?> getConvert(int cmd) {
        return this.targetObjectConvert.computeIfAbsent(cmd, (i) -> {
            EnhanceHandlerMethod method = support.getActionHandler(i);
            Class<?>[] types = method.getParamsType();
            for (Class<?> type : types) {
                if (convertManager.isInnerConvert(type)) {
                    continue;
                }
                ParamConvert<?> convert = convertManager.getConvert(type);
                if (Objects.isNull(convert)) {
                    throw new IllegalStateException(method.getMethod().getName() + " param type not has convert");
                }
                return convert;
            }
            return new VoidParamConvert();
        });
    }

    private class ProcessTask implements Runnable {

        private ServerExchange exchange;

        public ProcessTask(ServerExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public void run() {
            requestHandler.handler(exchange);
        }
    }

    private static class VoidParamConvert implements ParamConvert<Void> {

        @Override
        public Void convert(Request request) {
            return null;
        }
    }

    private static class DefaultPool implements RequestProcessPool {

        private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        @Override
        public void submit(ServerExchange exchange, Runnable runnable) {
            service.submit(runnable);
        }

        @Override
        public void submit(Session session, Runnable runnable) {
            service.submit(runnable);
        }
    }
}
