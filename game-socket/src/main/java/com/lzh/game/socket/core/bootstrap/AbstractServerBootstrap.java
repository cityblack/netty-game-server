package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private NetServer netServer;

    private final ServerSupport support;

    public AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<Session> sessionManage) {
        super(properties, sessionManage);
        this.sessionManage = sessionManage;
        this.support = new ServerSupport();
    }

    protected abstract NetServer createServer(int port);

    @Override
    protected void init(GameServerSocketProperties properties) {
        this.support.init();
        this.netServer = createServer(getPort());
    }

    @Override
    public void asyncStart() {
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
