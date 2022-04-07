package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.scoket.AbstractBootstrap;
import com.lzh.game.common.scoket.GameIoHandler;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.GameServerSocketProperties;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private NetServer netServer;

    public AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<Session> sessionManage, MessageHandler handler) {
        super(properties, handler);
        this.sessionManage = sessionManage;
    }

    protected abstract NetServer createServer(int port);

    public SessionManage<Session> getSessionManage() {
        return sessionManage;
    }

    public GameIoHandler<Session> getServerIoHandler() {
        return new GameIoHandler<>(getHandler(), sessionManage);
    }

    @Override
    protected void init(GameServerSocketProperties properties) {
        this.netServer = createServer(getPort());
    }

    @Override
    public void asyncStart() {
        this.netServer.asyncStart();
    }

    @Override
    public void start() {
        this.netServer.start();
    }

    @Override
    public void stop() {
        this.netServer.stop();
    }

    @Override
    public int getPort() {
        return this.properties.getPort();
    }
}
