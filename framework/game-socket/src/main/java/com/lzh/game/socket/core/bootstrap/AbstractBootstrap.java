package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.AtomicLifCycle;
import com.lzh.game.socket.core.LifeCycle;
import com.lzh.game.socket.core.process.MessageHandlerImpl;
import com.lzh.game.socket.core.process.Processor;
import com.lzh.game.socket.core.process.ProcessorManager;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;
import com.lzh.game.socket.core.session.impl.GameSession;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();

    protected T properties;

    protected ProcessorManager processorManager;

    protected SessionManage<? extends Session> sessionManage;

    protected GameIoHandler<? extends Session> ioHandler;

    protected AbstractBootstrap(T properties
            , SessionManage<? extends Session> sessionManage) {
        this.properties = properties;
        this.sessionManage = sessionManage;

        this.processorManager = new ProcessorManager();
        this.ioHandler = new GameIoHandler<>(new MessageHandlerImpl(processorManager), this.sessionManage);
    }

    protected AbstractBootstrap(T properties) {
        this(properties, defaultSession());
    }

    public static SessionManage<Session> defaultSession() {
        SessionMemoryCacheManage<String, Session> cacheManage = new GameSessionMemoryCacheManage<>();
        SessionFactory<Session> factory = GameSession::of;
        return new GameSessionManage<>(factory, cacheManage);
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

    public ProcessorManager getProcessManager() {
        return processorManager;
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

    public void addProcess(Class<?> command, Processor<?> process) {
        this.processorManager.registerProcess(command, process);
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
