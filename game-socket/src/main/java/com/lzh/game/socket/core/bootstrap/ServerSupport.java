package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Server extends utils
 */
class ServerSupport {

    private RequestActionSupport<EnhanceHandlerMethod> methodSupport;

    private InvokeMethodArgumentValues<Request> argumentValues;

    private RequestHandler handler;

    private List<Filter> filters = new ArrayList<>();

    public void setMethodSupport(RequestActionSupport<EnhanceHandlerMethod> methodSupport) {
        this.methodSupport = methodSupport;
    }

    public void setArgumentValues(InvokeMethodArgumentValues argumentValues) {
        this.argumentValues = argumentValues;
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }

    public void registerBean(int requestCmd, HandlerMethod method, int responseCmd) {

    }

    public void addInvokeBean(Object bean) {

    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public void init() {
        if (Objects.isNull(methodSupport)) {
            methodSupport = new DefaultActionMethodSupport();
        }
        if (Objects.isNull(argumentValues)) {
            argumentValues = new InvokeMethodArgumentValuesImpl();
        }
        if (Objects.isNull(handler)) {
            handler = new FilterHandler(this.filters, new ActionRequestHandler(methodSupport, argumentValues));
        }
    }
}
