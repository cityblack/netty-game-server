package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.convert.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.ErrorHandler;
import com.lzh.game.framework.socket.core.invoke.support.InterceptorHandler;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.exception.NotDefinedResponseProtocolException;
import com.lzh.game.framework.socket.exception.NotFondProtocolException;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ActionRequestHandler implements RequestDispatch {

    private final ErrorHandler errorHandler;

    private final InterceptorHandler interceptorHandler;

    private final InvokeMethodArgumentValues transfer;

    private final InvokeSupport support;

    public ActionRequestHandler() {
        this(new DefaultActionInvokeSupport(), new DefaultInvokeMethodArgumentValues());
    }

    public ActionRequestHandler(InvokeSupport support, InvokeMethodArgumentValues transfer) {
        this(support, transfer, null, null);
    }

    public ActionRequestHandler(InvokeSupport support, InvokeMethodArgumentValues transfer, ErrorHandler errorHandler, InterceptorHandler interceptorHandler) {
        this.errorHandler = errorHandler;
        this.interceptorHandler = interceptorHandler;
        this.transfer = transfer;
        this.support = support;
    }

    protected void executeAction(Request request, Response response) {
        var msgId = request.getMsgId();
        var method = support.getActionHandler(msgId);
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

    private Object invokeForRequest(Request request, EnhanceMethodInvoke handlerMethod) throws Exception {
        Object[] args = transfer.transfer(request, handlerMethod);
        if (isIntercept(request, handlerMethod, args)) {
            return null;
        }
        return handlerMethod.invoke(args);
    }

    private boolean isIntercept(Request request, EnhanceMethodInvoke handlerMethod, Object[] args) {
        return Objects.nonNull(this.interceptorHandler) && this.interceptorHandler.isIntercept(request, handlerMethod, args);
    }

    private boolean resolveException(Exception ex, Request request, Response response) {
        if (Objects.isNull(this.errorHandler)) {
            return false;
        }
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

}
