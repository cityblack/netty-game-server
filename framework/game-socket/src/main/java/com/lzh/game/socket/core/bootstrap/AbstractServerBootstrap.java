package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.DefaultActionInvokeSupport;
import com.lzh.game.socket.core.invoke.InvokeMethodArgumentValuesImpl;
import com.lzh.game.socket.core.invoke.InvokeUtils;
import com.lzh.game.socket.core.process.ActionRequestHandler;
import com.lzh.game.socket.core.process.DefaultRequestProcess;
import com.lzh.game.socket.core.protocol.Request;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private NetServer netServer;

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

        if (Objects.isNull(getMethodSupport())) {
            setMethodSupport(new DefaultActionInvokeSupport());
            if (!this.beans.isEmpty()) {
                for (Object bean : this.beans) {
                    this.addInvokeBean0(bean);
                }
            }
        }
//        if (!getProcessManager().hasProcessor(Request.class)) {
//            var dispatch = new ActionRequestHandler(getMethodSupport(), new InvokeMethodArgumentValuesImpl());
//            addProcessor(Request.class, new DefaultRequestProcess(new FilterHandler(this.filters, dispatch)));
//        }
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
        List<InvokeUtils.InvokeModel> list = InvokeUtils.parseBean(bean);
        for (InvokeUtils.InvokeModel model : list) {
            getMethodSupport().register(model.getValue(), model.getHandlerMethod());
        }
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }
}
