package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.NetServer;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.process.impl.AuthProcessor;
import com.lzh.game.framework.socket.core.session.Session;

public class ServerBootstrap<T extends ServerSocketProperties>
        extends AbstractBootstrap<T> implements GameServer {
    private NetServer netServer;

    public ServerBootstrap(BootstrapContext<T> context, NetServer netServer) {
        super(context);
        this.netServer = netServer;
    }

    @Override
    protected void doInit(T properties) {
        this.netServer.init(properties, this::onServerEvent);
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
    public boolean shutDown() {
        this.netServer.shutDown();
        return super.shutDown();
    }

    @Override
    public int getPort() {
        return getProperties().getPort();
    }

    @Override
    protected void addDefaultProcessor() {
        if (getProperties().isAbleAuth()) {
            context.getPipeline().addFirst(new AuthProcessor(context));
        }
        super.addDefaultProcessor();
    }

    public NetServer getNetServer() {
        return netServer;
    }

    public void setNetServer(NetServer netServer) {
        this.netServer = netServer;
    }

    private void onServerEvent(ProcessEvent event, Session session) {
        this.getContext().getPipeline().fireEvent(event, session);
    }
}
