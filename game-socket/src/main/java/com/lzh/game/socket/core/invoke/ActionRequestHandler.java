package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.*;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.invoke.support.ErrorHandler;
import com.lzh.game.socket.core.invoke.support.InterceptorHandler;
import com.lzh.game.socket.exception.NotDefinedResponseProtocolException;
import com.lzh.game.socket.exception.NotFondProtocolException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ActionRequestHandler implements RequestHandler {

    private ErrorHandler errorHandler;

    private InterceptorHandler interceptorHandler;

    private InvokeMethodArgumentValues<Request> transfer;

    private ActionMethodSupport<EnhanceHandlerMethod> support;

    public ActionRequestHandler(ActionMethodSupport<EnhanceHandlerMethod> support, InvokeMethodArgumentValues<Request> transfer) {
        this(support, transfer, new NoneErrorHandler(), new NoneInterceptorHandler());
    }

    public ActionRequestHandler(ActionMethodSupport<EnhanceHandlerMethod> support, InvokeMethodArgumentValues<Request> transfer, ErrorHandler errorHandler, InterceptorHandler interceptorHandler) {
        this.errorHandler = errorHandler;
        this.interceptorHandler = interceptorHandler;
        this.transfer = transfer;
        this.support = support;
    }

    protected void executeAction(ServerExchange exchange) {

        GameResponse response = (GameResponse) exchange.getResponse();
        GameRequest request = (GameRequest) exchange.getRequest();
        int cmd = request.cmd();
        EnhanceHandlerMethod method = support.getActionHandler(cmd);
        if (Objects.isNull(method)) {
            this.onError(exchange, new NotFondProtocolException(cmd));
            return;
        }
        try {
            Object o = invokeForRequest(request, method);
            response.setData(o);
            onSuccess(exchange, !method.isVoid());
        } catch (Exception e) {
            boolean resolved = resolveException(e, request, response);
            if (!resolved) {
                onError(exchange, e);
            }
        }
    }

    private Object invokeForRequest(Request request, HandlerMethod handlerMethod) throws Exception {
        Object[] args = transfer.transfer(request, handlerMethod);
        if (isIntercept(request, handlerMethod, args)) {
            return null;
        }
        Object returnValue = handlerMethod.doInvoke(args);
        return returnValue;
    }

    private boolean isIntercept(Request request, HandlerMethod handlerMethod, Object[] args) {
        return this.interceptorHandler.isIntercept(request, handlerMethod, args);
    }

    private boolean resolveException(Exception ex, GameRequest request, GameResponse response) {
        return this.errorHandler.resolveException(ex, request, response);
    }

    private void onSuccess(ServerExchange serverExchange, boolean hasResult) {
        if (hasResult) {
            Response response = serverExchange.getResponse();
            serverExchange.getSession().write(response);
        }
    }

    private void onError(ServerExchange exchange, Throwable throwable) {
        if (throwable instanceof NotDefinedResponseProtocolException) {
            log.error("Not register response cmd. Request cmd:{}", exchange.getRequest().cmd());
        } else if (throwable instanceof NotFondProtocolException) {
            log.error("Not register request cmd:{}", exchange.getRequest().cmd());
        } else {
            log.error("Request error. cmd:{}", exchange.getRequest().cmd(), throwable);
        }
    }

    @Override
    public void handler(ServerExchange exchange) {
        executeAction(exchange);
    }

    private static class NoneErrorHandler implements ErrorHandler {
        @Override
        public boolean resolveException(Exception ex, GameRequest request, GameResponse response) {
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
