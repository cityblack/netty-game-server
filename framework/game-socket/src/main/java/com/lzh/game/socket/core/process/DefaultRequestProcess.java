package com.lzh.game.socket.core.process;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.InvokeSupport;
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

    private InvokeSupport<?> support;

    private RequestProcessPool pool;

    public DefaultRequestProcess(RequestHandle requestHandle, InvokeSupport<EnhanceHandlerMethod> support) {
        this(requestHandle, new DefaultPool(), support);
    }

    public DefaultRequestProcess(RequestHandle requestHandle, RequestProcessPool pool
            , InvokeSupport<?> support) {
        this.requestHandle = requestHandle;
        this.support = support;
        this.pool = pool;
    }

    @Override
    public void process(Request request) {
        int msgId = request.getMsgId();
        if (!support.containMapping(msgId)) {
            log.warn("Not find define cmd:{}", msgId);
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
