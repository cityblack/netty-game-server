package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.socket.GameIoHandler;
import com.lzh.game.socket.GameSocketProperties;
import com.lzh.game.socket.core.LifeCycle;
import com.lzh.game.socket.core.MessageHandlerImpl;
import com.lzh.game.socket.core.ProcessManager;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private static final byte NO_STARED = 0x00;
    private static final byte RUNNING = 0x01;
    private static final byte STARED = 0x02;

    protected final AtomicInteger STATUS = new AtomicInteger(NO_STARED);

    protected T properties;

    protected ProcessManager processManager;

    protected SessionManage<Session> sessionManage;

    protected GameIoHandler<Session> ioHandler;

    public AbstractBootstrap(T properties
            , SessionManage<Session> sessionManage) {
        this.properties = properties;
        this.sessionManage = sessionManage;

        this.processManager = new ProcessManager();
        this.ioHandler = new GameIoHandler<>(new MessageHandlerImpl(processManager), this.sessionManage);
    }

    public T getProperties() {
        return properties;
    }

    public SessionManage<Session> getSessionManage() {
        return sessionManage;
    }

    public GameIoHandler<Session> getIoHandler() {
        return ioHandler;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    protected abstract void init(T properties);

    protected abstract void startup();

    @Override
    public void start() {
        if (STATUS.compareAndSet(NO_STARED, RUNNING)) {
            return;
        }
        init(this.properties);
        startup();
        STATUS.incrementAndGet();
    }

    @Override
    public boolean isStared() {
        return STATUS.get() == STARED;
    }

    @Override
    public void shutDown() {
        if (STATUS.compareAndSet(STARED, RUNNING)) {
            return;
        }
    }
}
