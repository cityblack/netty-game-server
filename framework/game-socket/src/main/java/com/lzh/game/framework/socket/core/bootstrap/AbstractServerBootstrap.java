package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.utils.InvokeUtils;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.GameServerSocketProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private NetServer netServer;

    private final List<Filter> filters = new ArrayList<>();

    private final List<Object> beans = new ArrayList<>();

    protected AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<Session> sessionManage, MessageManager messageManager) {
        super(properties, sessionManage, messageManager);
    }

    protected AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<Session> sessionManage) {
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
        this.netServer = createServer(getPort());
        this.filters.clear();
        this.beans.clear();
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
        var list = InvokeUtils.parseBean(bean);
        for (InvokeUtils.InvokeModel model : list) {
            getMethodSupport().register(model.getValue(), model.getHandlerMethod());
            if (Objects.nonNull(model.getNewProtoClass())) {
                getMessageManager().addMessage(model.getNewProtoClass());
            }
        }
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }
}
