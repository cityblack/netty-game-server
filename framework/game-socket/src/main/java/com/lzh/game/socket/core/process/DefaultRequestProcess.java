package com.lzh.game.socket.core.process;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.*;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parse target Object data earlier
 */
@Slf4j
public class DefaultRequestProcess implements Process<Request> {

    private RequestHandle requestHandle;

    private ActionMethodSupport<?> support;

    private RequestProcessPool pool;

    public DefaultRequestProcess(RequestHandle requestHandle, ActionMethodSupport<EnhanceHandlerMethod> support) {
        this(requestHandle, new DefaultPool(), support);
    }

    public DefaultRequestProcess(RequestHandle requestHandle, RequestProcessPool pool
            , ActionMethodSupport<?> support) {
        this.requestHandle = requestHandle;
        this.support = support;
        this.pool = pool;
    }

    @Override
    public void process(RemoteContext context, Request request) {

        int cmd = request.cmd();
        if (!support.containMapping(cmd)) {
            log.warn("Not find define cmd:{}", cmd);
            return;
        }
        pool.submit(context, new ProcessTask(context));
    }


    private class ProcessTask implements Runnable {

        private RemoteContext context;

        public ProcessTask(RemoteContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            requestHandle.handle(context);
        }
    }


    private static class DefaultPool implements RequestProcessPool {

        private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        @Override
        public void submit(RemoteContext exchange, Runnable runnable) {
            service.submit(runnable);
        }

        @Override
        public void submit(Session session, Runnable runnable) {
            service.submit(runnable);
        }
    }
}
