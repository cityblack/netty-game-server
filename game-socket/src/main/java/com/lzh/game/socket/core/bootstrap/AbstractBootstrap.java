package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameIoHandler;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.core.AtomicLifCycle;
import com.lzh.game.socket.core.LifeCycle;
import com.lzh.game.socket.core.MessageHandlerImpl;
import com.lzh.game.socket.core.process.Process;
import com.lzh.game.socket.core.process.ProcessManager;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();

    protected T properties;

    protected ProcessManager processManager;

    protected SessionManage<? extends Session> sessionManage;

    protected GameIoHandler<? extends Session> ioHandler;

    protected AbstractBootstrap(T properties
            , SessionManage<? extends Session> sessionManage) {
        this.properties = properties;
        this.sessionManage = sessionManage;

        this.processManager = new ProcessManager();
        this.ioHandler = new GameIoHandler<>(new MessageHandlerImpl(processManager), this.sessionManage);
    }

    protected AbstractBootstrap(T properties) {
        this(properties, defaultSession());
    }

    public static SessionManage<Session> defaultSession() {
        SessionMemoryCacheManage<String, Session> cacheManage = new GameSessionMemoryCacheManage<>();
        SessionFactory<Session> factory = GameSession::of;
        return new GameSessionManage<>(cacheManage, factory);
    }

    public T getProperties() {
        return properties;
    }

    public <S extends Session>SessionManage<S> getSessionManage() {
        return (SessionManage<S>) sessionManage;
    }

    public GameIoHandler<? extends Session> getIoHandler() {
        return ioHandler;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    protected abstract void doInit(T properties);

    protected abstract void startup();

    protected abstract void asyncStartup();

    @Override
    public void start() {
        if (STATUS.running()) {
            doInit(this.properties);
            STATUS.start();
            startup();
        }
    }

    public <E extends RemotingCommand>void addProcess(int key, Process<E> process) {
        this.processManager.registerProcess(key, process);
    }

    @Override
    public boolean isStared() {
        return STATUS.isStared();
    }


    @Override
    public void shutDown() {
        STATUS.shutDown();
    }

    @Override
    public void asyncStart() {
        if (STATUS.running()) {
            doInit(this.properties);
            asyncStartup();
            STATUS.start();
        }
    }
}
