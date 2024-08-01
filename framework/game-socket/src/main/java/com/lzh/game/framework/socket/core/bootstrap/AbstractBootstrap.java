package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.handler.GameIoHandler;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterHandler;
import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.invoke.RequestDispatch;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.HeartbeatProcessor;
import com.lzh.game.framework.socket.core.protocol.HeartbeatProtocol;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import com.lzh.game.framework.socket.core.protocol.serial.impl.fury.FurySerialize;
import com.lzh.game.framework.socket.utils.Constant;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();

    protected T properties;

    protected BootstrapContext context;

    private final GameIoHandler ioHandler;

    protected List<Filter> filters = new CopyOnWriteArrayList<>();

    public AbstractBootstrap(T properties, BootstrapContext context) {
        this.properties = properties;
        this.context = context;
        this.ioHandler = new GameIoHandler(context);
    }

    public AbstractBootstrap(T properties) {
        this(properties, BootstrapContext.of());
    }

    public T getProperties() {
        return properties;
    }

    public BootstrapContext getContext() {
        return context;
    }

    protected abstract void doInit(T properties);

    protected abstract void startup();

    protected abstract void asyncStartup();

    @Override
    public void start() {
        if (STATUS.running()) {
            init();
            doInit(this.properties);
            startup();
            STATUS.start();
        }
    }

    protected void init() {
        MessageSerializeManager.getInstance()
                .registerMessage(Constant.DEFAULT_SERIAL_SIGN, new FurySerialize(context.getMessageManager(), properties.getFury()));
        this.addDefaultProtocol();
        this.addDefaultProcessor();
    }

    protected void addDefaultProtocol() {
        var message = context.getMessageManager();
        message.addMessage(HeartbeatProtocol.class);
    }

    protected void addDefaultProcessor() {

//        if (properties.isUseDefaultRequest()) {
//            var dispatch = new ActionRequestHandler(context, new DefaultInvokeMethodArgumentValues());
//            var filter = new FilterHandler(this.filters, dispatch);
//            context.getPipeline().addFirst(new DefaultRequestProcess(filter));
//        }
        context.getPipeline().addFirst(new HeartbeatProcessor());
    }

    public void addInvokeBean(Object bean) {
        context.getBeanHelper().parseBean(bean, method -> method.isAnnotationPresent(Receive.class));
    }

    public void addProcessor(Processor process) {
        if (process instanceof DefaultRequestProcess requestProcess) {
            if (!(requestProcess.getDispatch() instanceof FilterHandler)) {
                requestProcess.setDispatch(new FilterHandler(this.filters, requestProcess.getDispatch()));
            }
        }
        context.getPipeline().addLast(process);
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

    public GameIoHandler getIoHandler() {
        return ioHandler;
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }
}
