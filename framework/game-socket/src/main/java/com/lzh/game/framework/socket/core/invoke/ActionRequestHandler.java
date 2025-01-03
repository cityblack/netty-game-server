package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.common.method.EnhanceMethodInvoke;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.invoke.support.ErrorHandler;
import com.lzh.game.framework.socket.core.invoke.support.InterceptorHandler;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.exception.NotDefinedResponseProtocolException;
import com.lzh.game.framework.socket.exception.NotFondProtocolException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class ActionRequestHandler implements RequestDispatch {

    private final ErrorHandler errorHandler;

    private final InterceptorHandler interceptorHandler;

    private final BootstrapContext context;

    public ActionRequestHandler(BootstrapContext context) {
        this(context, null, null);
    }

    public ActionRequestHandler(BootstrapContext context, ErrorHandler errorHandler, InterceptorHandler interceptorHandler) {
        this.errorHandler = errorHandler;
        this.interceptorHandler = interceptorHandler;
        this.context = context;
    }

    protected void executeAction(RequestContext context) {
        var request = context.getRequest();
        var response = context.getResponse();
        var msgId = request.getMsgId();
        var method = this.context.getInvokeSupport().getActionHandler(msgId);
        if (Objects.isNull(method)) {
            this.onError(request, response, new NotFondProtocolException(msgId));
            return;
        }
        try {
            Object o = invokeForRequest(request, method);
            onSuccess(context, o, !method.isVoid());
        } catch (Exception e) {
            boolean resolved = resolveException(e, request, response);
            if (!resolved) {
                onError(request, response, e);
            }
        }
    }

    private Object invokeForRequest(Request request, EnhanceMethodInvoke handlerMethod) throws Exception {
        Object[] args = context.getValues().transfer(request, handlerMethod);
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

    private void onSuccess(RequestContext context, Object data, boolean hasResult) {
        var response = context.getResponse();
        var request = context.getRequest();
        if (!request.isOneWay() && hasResult) {
            if (Objects.isNull(data)) {
                log.error("Request {} need return data. But the result is null.", request.getMsgId());
                return;
            }
            var type = data.getClass();
            response.setDataClass(type);
            response.setData(data);
            var defined = this.context.getMessageManager()
                    .findDefine(type);
            response.setMsgId(defined.getMsgId());
            request.getSession().write(response);
            if (Objects.nonNull(context.getBack())) {
                context.getBack().accept(context);
            }
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
    public void handle(RequestContext context) {
        executeAction(context);
    }
}
