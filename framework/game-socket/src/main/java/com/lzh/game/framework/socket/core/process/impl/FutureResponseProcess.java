package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FutureResponseProcess implements Processor {

    private ProcessorExecutorService service;

    public FutureResponseProcess() {
        this(new DefaultService());
    }

    public FutureResponseProcess(ProcessorExecutorService service) {
        this.service = service;
    }

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        RequestFuture.received((Response) data, false);
    }

    @Override
    public boolean match(Session session, Object msg) {
        return msg instanceof Response;
    }

    @Override
    public ProcessorExecutorService service() {
        return service;
    }

    static class DefaultService implements ProcessorExecutorService {

        private ExecutorService service = Executors.newCachedThreadPool();

        @Override
        public void submit(Session session, Object msg, Runnable runnable) {
            service.submit(runnable);
        }
    }
}
