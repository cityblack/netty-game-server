package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.scoket.AbstractBootstrap;
import com.lzh.game.common.scoket.GameIoHandler;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.SocketHandlerBuilder;
import com.lzh.game.socket.config.GameServerSocketProperties;
import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;
import org.springframework.context.ApplicationContext;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private SessionManage<Session> sessionManage;

    private MessageHandler messageHandler;

    private GameIoHandler serverIoHandler;

    private NetServer netServer;

    public AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<Session> sessionManage) {
        super(properties);
        this.sessionManage = sessionManage;
    }

    @Override
    public MessageHandler messageHandler() {
        return messageHandler;
    }

    protected void setRequestHandle(ApplicationContext context) {
        SocketHandlerBuilder builder = SocketHandlerBuilder.application(context);
        this.messageHandler = builder.build();
    }

    @Override
    protected void init(ApplicationContext context) {
        this.setRequestHandle(context);
        this.serverIoHandler = new GameIoHandler(messageHandler, sessionManage);
        this.netServer = createServer(getPort());
    }

    protected abstract NetServer createServer(int port);

    public SessionManage<Session> getSessionManage() {
        return sessionManage;
    }

    public GameIoHandler getServerIoHandler() {
        return serverIoHandler;
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
    protected void doStart() {
        this.start();
    }

    @Override
    public int getPort() {
        return this.properties.getPort();
    }
}
