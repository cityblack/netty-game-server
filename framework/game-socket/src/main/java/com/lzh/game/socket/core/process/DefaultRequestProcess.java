package com.lzh.game.socket.core.process;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestDispatch;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parse target Object data earlier
 */
@Slf4j
public class DefaultRequestProcess implements Processor<Request> {

    private RequestDispatch dispatch;

    private ProcessorExecutorService<Request> executorService;

    public DefaultRequestProcess(RequestDispatch requestHandle) {
        this(requestHandle, new DefaultExecutorService());
    }

    public DefaultRequestProcess(RequestDispatch dispatch, ProcessorExecutorService<Request> executorService) {
        this.dispatch = dispatch;
        this.executorService = executorService;
    }

    @Override
    public void process(Session session, Request request) {
        executorService.submit(session, request, new ProcessTask(request));
    }


    private class ProcessTask implements Runnable {

        private Request request;

        public ProcessTask(Request request) {
            this.request = request;
        }

        @Override
        public void run() {
            dispatch.handle(request);
        }
    }


    private static class DefaultExecutorService implements ProcessorExecutorService<Request> {

        private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        @Override
        public void submit(Session session, Request command, Runnable runnable) {
            service.submit(runnable);
        }
    }
}
