package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.handler.GameIoHandler;
import com.lzh.game.framework.socket.core.filter.Filter;
import com.lzh.game.framework.socket.core.filter.FilterHandler;
import com.lzh.game.framework.socket.core.invoke.bean.InvokeBeanHelperHandler;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.impl.AuthProcessor;
import com.lzh.game.framework.socket.core.process.impl.DefaultRequestProcess;
import com.lzh.game.framework.socket.core.process.impl.HeartbeatProcessor;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import com.lzh.game.framework.socket.core.protocol.serial.rookie.RookieSerialize;
import com.lzh.game.framework.socket.utils.Constant;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractBootstrap<T extends GameSocketProperties>
        implements LifeCycle {

    private final AtomicLifCycle STATUS = new AtomicLifCycle();


    protected BootstrapContext<T> context;

    private final GameIoHandler ioHandler;

    public AbstractBootstrap(BootstrapContext<T> context) {
        this.context = context;
        this.ioHandler = new GameIoHandler(context);
    }

    public T getProperties() {
        return context.getProperties();
    }

    public BootstrapContext<T> getContext() {
        return context;
    }

    protected abstract void doInit(T properties);

    protected abstract void startup();

    protected abstract void asyncStartup();

    @Override
    public void start() {
        if (STATUS.running()) {
            init();
            doInit(getProperties());
            startup();
            STATUS.start();
        }
    }

    protected void init() {
        this.addDefaultProcessor();
        MessageSerializeManager.getInstance()
                .registerSerialize(Constant.DEFAULT_SERIAL_SIGN, new RookieSerialize(context.getMessageManager()));
    }

    protected void addDefaultProcessor() {
        context.getPipeline().addFirst(new HeartbeatProcessor());
        context.getPipeline().addFirst(new AuthProcessor(context));
    }

    public void addInvokeBean(Object bean) {
        InvokeBeanHelperHandler.getInstance().pushBean(bean, context);
    }

    public void addProcessor(Processor process) {
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
            doInit(getProperties());
            asyncStartup();
            STATUS.start();
        }
    }

    public GameIoHandler getIoHandler() {
        return ioHandler;
    }
}
