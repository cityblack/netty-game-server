package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.framework.socket.core.invoke.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.cache.SessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import com.lzh.game.framework.socket.GameSocketProperties;
import com.lzh.game.framework.socket.core.AtomicLifCycle;
import com.lzh.game.framework.socket.core.LifeCycle;
import com.lzh.game.framework.socket.core.process.MessageHandlerImpl;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorManager;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();

    protected T properties;

    protected ProcessorManager processorManager;

    protected SessionManage<? extends Session> sessionManage;

    protected GameIoHandler<? extends Session> ioHandler;

    private MessageManager messageManager;

    private InvokeSupport<EnhanceHandlerMethod> methodSupport;

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

    public <S extends Session> SessionManage<S> getSessionManage() {
        return (SessionManage<S>) sessionManage;
    }

    public GameIoHandler<? extends Session> getIoHandler() {
        return ioHandler;
    }

    public ProcessorManager getProcessManager() {
        return processorManager;
    }

    public InvokeSupport<EnhanceHandlerMethod> getMethodSupport() {
        return methodSupport;
    }

    public void setMethodSupport(InvokeSupport<EnhanceHandlerMethod> methodSupport) {
        this.methodSupport = methodSupport;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
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

    public void addProcessor(Processor<?> process) {
        this.processorManager.registerProcessor(command, process);
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
