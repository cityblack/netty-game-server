package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.GameSocketProperties;
import com.lzh.game.framework.socket.core.AtomicLifCycle;
import com.lzh.game.framework.socket.core.LifeCycle;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.context.DefaultProcessorPipeline;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.message.DefaultMessageManager;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.cache.SessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;

import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();

    protected T properties;

    protected SessionManage<Session> sessionManage;

    private MessageManager messageManager;

    private InvokeSupport methodSupport;

    private ProcessorPipeline pipeline;

    private GameIoHandler ioHandler;

    protected AbstractBootstrap(T properties
            , SessionManage<Session> sessionManage, MessageManager messageManager) {
        this.properties = properties;
        this.sessionManage = sessionManage;
        this.pipeline = new DefaultProcessorPipeline();
        this.messageManager = messageManager;
        this.ioHandler = new GameIoHandler(pipeline, sessionManage);
    }

    protected AbstractBootstrap(T properties, SessionManage<Session> sessionManage) {
        this(properties, sessionManage, new DefaultMessageManager(new ConcurrentHashMap<>()));
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

    public SessionManage<Session> getSessionManage() {
        return sessionManage;
    }

    public InvokeSupport getMethodSupport() {
        return methodSupport;
    }

    public void setMethodSupport(InvokeSupport methodSupport) {
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

    public void addProcessor(Processor process) {
        this.pipeline.addLast(process);
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

    public ProcessorPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(ProcessorPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public GameIoHandler getIoHandler() {
        return ioHandler;
    }
}
