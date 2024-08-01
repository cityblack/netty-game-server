package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.NetServer;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;

public abstract class AbstractServerBootstrap<T extends GameServerSocketProperties>
        extends AbstractBootstrap<T> implements GameServer {

    private NetServer netServer;

    public AbstractServerBootstrap(T properties, BootstrapContext context) {
        super(properties, context);
    }

    public AbstractServerBootstrap(T properties) {
        super(properties);
    }

    protected abstract NetServer createServer(int port, T properties, ProcessorPipeline pipeline);

    @Override
    protected void doInit(T properties) {
        this.netServer = createServer(getPort(), properties, getContext().getPipeline());
        this.filters = null;
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

}
