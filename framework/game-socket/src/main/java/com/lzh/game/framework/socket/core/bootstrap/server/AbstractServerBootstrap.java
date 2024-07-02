package com.lzh.game.framework.socket.core.bootstrap.server;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.NetServer;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServerBootstrap<T extends GameServerSocketProperties>
        extends AbstractBootstrap<T> implements GameServer {

    private NetServer netServer;

    private List<Filter> filters = new ArrayList<>();

    public AbstractServerBootstrap(T properties) {
        super(properties);
    }

    protected abstract NetServer createServer(int port, T properties, ProcessorPipeline pipeline);

    @Override
    protected void doInit(T properties) {
        this.netServer = createServer(getPort(), properties, getPipeline());
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

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }
}
