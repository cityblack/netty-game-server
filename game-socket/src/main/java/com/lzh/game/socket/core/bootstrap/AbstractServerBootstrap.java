package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.ActionMethodSupport;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandle;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.process.DefaultRequestProcess;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private NetServer netServer;

    private ActionMethodSupport<EnhanceHandlerMethod> methodSupport;

    private InvokeMethodArgumentValues<Request> argumentValues;

    private RequestHandle handler;

    private ConvertManager convertManager;

    private List<Filter> filters = new ArrayList<>();

    private List<Object> beans = new ArrayList<>();

    protected AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<? extends Session> sessionManage) {
        super(properties, sessionManage);
    }

    protected AbstractServerBootstrap(GameServerSocketProperties properties) {
        super(properties);
    }

    protected abstract NetServer createServer(int port);

    @Override
    protected void doInit(GameServerSocketProperties properties) {
        if (Objects.isNull(convertManager)) {
            convertManager = new DefaultConvertManager();
        }
        if (Objects.isNull(argumentValues)) {
            argumentValues = new InvokeMethodArgumentValuesImpl(convertManager);
        }
        if (Objects.isNull(methodSupport)) {
            methodSupport = new DefaultActionMethodSupport();
            if (!this.beans.isEmpty()) {
                for (Object bean : this.beans) {
                    this.addInvokeBean0(bean);
                }
            }
        }
        if (Objects.isNull(handler)) {
            handler = new FilterHandler(this.filters, new ActionRequestHandler(methodSupport, argumentValues));
        }
        if (Objects.isNull(getProcessManager().getProcess(Constant.REQUEST_SIGN))) {
            addProcess(Constant.REQUEST_SIGN, new DefaultRequestProcess(handler, convertManager, methodSupport));
        }
        if (Objects.isNull(getProcessManager().getProcess(Constant.ONEWAY_SIGN))) {
            addProcess(Constant.ONEWAY_SIGN, new DefaultRequestProcess(handler, convertManager, methodSupport));
        }
        this.netServer = createServer(getPort());
    }

    @Override
    protected void asyncStartup() {
        this.netServer.asyncStart();
    }

    @Override
    protected void startup() {
        this.netServer.start();
    }

    @Override
    public void shutDown() {
        super.shutDown();
        this.netServer.shutDown();
    }

    @Override
    public int getPort() {
        return this.properties.getPort();
    }

    public void addInvokeBean(Object bean) {
        if (isStared()) {
            this.addInvokeBean0(bean);
        } else {
            this.beans.add(bean);
        }
    }

    private void addInvokeBean0(Object bean) {
        List<InvokeUtils.InvokeModel> list = InvokeUtils.parseBean(bean, convertManager.inner());
        for (InvokeUtils.InvokeModel model : list) {
            methodSupport.register(model.getValue(), model.getHandlerMethod());
            if (model.hasParam()) {
                convertManager.registerConvert(model.getParamClass(), new ProtoBufferConvert<>(model.getParamClass()));
            }
        }
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    // === set ====
    public AbstractServerBootstrap setMethodSupport(ActionMethodSupport<EnhanceHandlerMethod> methodSupport) {
        this.methodSupport = methodSupport;
        return this;
    }

    public AbstractServerBootstrap setArgumentValues(InvokeMethodArgumentValues<Request> argumentValues) {
        this.argumentValues = argumentValues;
        return this;
    }

    public AbstractServerBootstrap setConvertManager(ConvertManager convertManager) {
        this.convertManager = convertManager;
        return this;
    }

    public AbstractServerBootstrap setHandler(RequestHandle handler) {
        this.handler = handler;
        return this;
    }
}
