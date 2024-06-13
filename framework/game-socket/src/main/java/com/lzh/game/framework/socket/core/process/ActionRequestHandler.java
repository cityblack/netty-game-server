package com.lzh.game.framework.socket.core.process;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.framework.socket.core.invoke.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.InvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.ErrorHandler;
import com.lzh.game.framework.socket.core.invoke.support.InterceptorHandler;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.exception.NotDefinedResponseProtocolException;
import com.lzh.game.framework.socket.exception.NotFondProtocolException;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ActionRequestHandler implements RequestDispatch, Processor<Request> {

    private ErrorHandler errorHandler;

    private InterceptorHandler interceptorHandler;

    private InvokeMethodArgumentValues transfer;

    private InvokeSupport<EnhanceHandlerMethod> support;

    public ActionRequestHandler(InvokeSupport<EnhanceHandlerMethod> support, InvokeMethodArgumentValues transfer) {
        this(support, transfer, new NoneErrorHandler(), new NoneInterceptorHandler());
    }

    public ActionRequestHandler(InvokeSupport<EnhanceHandlerMethod> support, InvokeMethodArgumentValues transfer, ErrorHandler errorHandler, InterceptorHandler interceptorHandler) {
        this.errorHandler = errorHandler;
        this.interceptorHandler = interceptorHandler;
        this.transfer = transfer;
        this.support = support;
    }

    protected void executeAction(Request request, Response response) {
        int msgId = request.getMsgId();
        EnhanceHandlerMethod method = support.getActionHandler(msgId);
        if (Objects.isNull(method)) {
            this.onError(request, response, new NotFondProtocolException(msgId));
            return;
        }
        try {
            Object o = invokeForRequest(request, method);
            response.setData(o);
            onSuccess(request, response, !method.isVoid());
        } catch (Exception e) {
            boolean resolved = resolveException(e, request, response);
            if (!resolved) {
                onError(request, response, e);
            }
        }
    }

    private Object invokeForRequest(Request request, HandlerMethod handlerMethod) throws Exception {
        Object[] args = transfer.transfer(request, handlerMethod);
        if (isIntercept(request, handlerMethod, args)) {
            return null;
        }
        return handlerMethod.doInvoke(args);
    }

    private boolean isIntercept(Request request, HandlerMethod handlerMethod, Object[] args) {
        return this.interceptorHandler.isIntercept(request, handlerMethod, args);
    }

    private boolean resolveException(Exception ex, Request request, Response response) {
        return this.errorHandler.resolveException(ex, request, response);
    }

    private void onSuccess(Request request, Response response, boolean hasResult) {
        if (hasResult) {
            request.getSession().write(response);
        }
    }

    private void onError(Request request, Response response, Throwable throwable) {
        if (throwable instanceof NotDefinedResponseProtocolException) {
            log.error("Not register response cmd. Request msgId:{}", request.getMsgId());
        } else if (throwable instanceof NotFondProtocolException) {
            log.error("Not register request msgId:{}", request.getMsgId());
        } else {
            log.error("Request error. msgId:{}", request.getMsgId(), throwable);
        }
    }

    @Override
    public void handle(Request request) {
        Response response = Response.of(request);
        executeAction(request, response);
    }

    @Override
    public void process(Session session, Request request) {

    }

    @Override
    public boolean match(Request msg) {
        return false;
    }

    private static class NoneErrorHandler implements ErrorHandler {
        @Override
        public boolean resolveException(Exception ex, Request request, Response response) {
            return false;
        }
    }

    private static class NoneInterceptorHandler implements InterceptorHandler {
        @Override
        public boolean isIntercept(Request request, HandlerMethod handlerMethod, Object[] args) {
            return false;
        }
    }
}
