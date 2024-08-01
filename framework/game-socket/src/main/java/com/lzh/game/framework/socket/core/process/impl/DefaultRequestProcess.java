package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.invoke.RequestDispatch;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * Parse target Object data earlier
 */
@Slf4j
public class DefaultRequestProcess implements Processor {

    private RequestDispatch dispatch;

    private ProcessorExecutorService executorService;

    public DefaultRequestProcess(RequestDispatch requestHandle) {
        this.dispatch = requestHandle;
    }

    public DefaultRequestProcess(RequestDispatch dispatch, ProcessorExecutorService executorService) {
        this.dispatch = dispatch;
        this.executorService = executorService;
    }

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        Request request = (Request) data;
        dispatch.handle(request, response -> requestBack(context, session, response));
    }

    public static void requestBack(ProcessorContext context, Session session, Object data) {
        context.fireReceive(session, data);
    }

    @Override
    public boolean match(Session session, Object msg) {
        return msg instanceof Request;
    }

    @Override
    public ProcessorExecutorService service() {
        return executorService;
    }

    public RequestDispatch getDispatch() {
        return dispatch;
    }

    public void setDispatch(RequestDispatch dispatch) {
        this.dispatch = dispatch;
    }
}
