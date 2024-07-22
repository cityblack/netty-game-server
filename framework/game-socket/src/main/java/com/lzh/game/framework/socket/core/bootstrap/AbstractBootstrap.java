package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterHandler;
import com.lzh.game.framework.socket.core.invoke.ActionRequestHandler;
import com.lzh.game.framework.socket.core.invoke.InvokeBeanHelper;
import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.DefaultActionInvokeSupport;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.context.DefaultProcessorPipeline;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.protocol.message.DefaultMessageManager;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import com.lzh.game.framework.socket.core.protocol.serial.impl.fury.FurySerialize;
import com.lzh.game.framework.socket.core.session.GameSessionManage;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionFactory;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.cache.SessionMemoryCacheManage;
import com.lzh.game.framework.socket.core.session.impl.GameSession;
import com.lzh.game.framework.socket.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();

    protected T properties;

    protected SessionManage<Session> sessionManage;

    private MessageManager messageManager;

    private InvokeSupport invokeSupport;

    private InvokeBeanHelper beanHelper;

    private final GameIoHandler ioHandler;

    protected List<Object> beans = new ArrayList<>();

    protected List<Processor> processors = new ArrayList<>();

    protected List<Filter> filters = new CopyOnWriteArrayList<>();

    private final ProcessorPipeline pipeline;

    public AbstractBootstrap(T properties
            , SessionManage<Session> sessionManage
            , MessageManager messageManager
            , InvokeSupport invokeSupport) {
        this.properties = properties;
        this.sessionManage = sessionManage;
        this.messageManager = messageManager;
        this.invokeSupport = invokeSupport;
        this.pipeline = new DefaultProcessorPipeline();
        this.ioHandler = new GameIoHandler(pipeline, sessionManage);
    }

    public AbstractBootstrap(T properties, SessionManage<Session> sessionManage) {
        this.properties = properties;
        this.sessionManage = sessionManage;
        this.pipeline = new DefaultProcessorPipeline();
        this.ioHandler = new GameIoHandler(pipeline, sessionManage);
    }

    public AbstractBootstrap(T properties) {
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

    public InvokeSupport getInvokeSupport() {
        return invokeSupport;
    }

    public void setInvokeSupport(InvokeSupport invokeSupport) {
        this.invokeSupport = invokeSupport;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    protected abstract void doInit(T properties);

    protected abstract void startup();

    protected abstract void asyncStartup();

    @Override
    public void start() {
        if (STATUS.running()) {
            init();
            doInit(this.properties);
            STATUS.start();
            startup();
        }
    }

    protected void init() {
        checkField();
        MessageSerializeManager.getInstance()
                .registerMessage(Constant.DEFAULT_SERIAL_SIGN, new FurySerialize(messageManager, properties.getFury()));
        this.addDefaultProcessor();
        if (!this.beans.isEmpty()) {
            for (Object bean : this.beans) {
                this.addInvokeBean0(bean);
            }
        }
        // free mem.
        this.beans = null;
        this.processors = null;
    }

    protected void addDefaultProcessor() {

        if (properties.isUseDefaultRequest()) {
            var dispatch = new ActionRequestHandler(invokeSupport, new DefaultInvokeMethodArgumentValues());
            var filter = new FilterHandler(this.filters, dispatch);
            this.processors.add(new DefaultRequestProcess(filter));
        }

        for (Processor processor : this.processors) {
            pipeline.addLast(processor);
        }
    }

    protected void checkField() {
        if (Objects.isNull(invokeSupport)) {
            this.invokeSupport = new DefaultActionInvokeSupport();
        }
        if (Objects.isNull(messageManager)) {
            this.messageManager = new DefaultMessageManager();
        }
        this.beanHelper = new InvokeBeanHelper(this.invokeSupport, this.messageManager);
    }

    public void addInvokeBean(Object bean) {
        if (isStared()) {
            this.addInvokeBean0(bean);
        } else {
            this.beans.add(bean);
        }
    }

    protected void addInvokeBean0(Object bean) {
        getBeanHelper().parseBean(bean, method -> method.isAnnotationPresent(Receive.class));
    }

    public void addProcessor(Processor process) {
        if (isStared()) {
            this.pipeline.addLast(process);
        } else {
            this.processors.add(process);
        }
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
            init();
            doInit(this.properties);
            asyncStartup();
            STATUS.start();
        }
    }

    public ProcessorPipeline getPipeline() {
        return pipeline;
    }

    public GameIoHandler getIoHandler() {
        return ioHandler;
    }

    public InvokeBeanHelper getBeanHelper() {
        return beanHelper;
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }
}
