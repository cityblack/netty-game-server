package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.SocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.handler.GameIoHandler;
import com.lzh.game.framework.socket.core.invoke.bean.InvokeBeanHelperHandler;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.process.impl.HeartbeatProcessor;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import com.lzh.game.framework.socket.core.protocol.serial.rookie.RookieSerialize;
import com.lzh.game.framework.socket.Constant;
import com.lzh.game.framework.socket.utils.ShutdownHook;

public abstract class AbstractBootstrap<T extends SocketProperties>
        implements LifeCycle {

    private static final String SHUTDOWN_HOOK_THREAD_NAME = "SocketShutdownHook";
    private final AtomicLifCycle STATUS = new AtomicLifCycle();
    private Thread shutdownHook;

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
        this.registerShutdownHook();
    }

    private void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    shutDown();
                    ShutdownHook.shutdown();
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    protected void addDefaultProcessor() {
        context.getPipeline().addFirst(new HeartbeatProcessor());
    }

    public void addInvokeBean(Object bean) {
        InvokeBeanHelperHandler.getInstance().pushBean(bean, context);
    }

    public void addProcessor(Processor process) {
        context.getPipeline().addLast(process);
    }

    public void addProcessEventListen(ProcessEvent event, ProcessEventListen listen) {
        context.getPipeline().addProcessEventListen(event, listen);
    }

    @Override
    public boolean isStared() {
        return STATUS.isStared();
    }

    @Override
    public boolean shutDown() {
        if (STATUS.shutDown()) {
            context.shutdown();
            return true;
        }
        return false;
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

    public Thread getShutdownHook() {
        return shutdownHook;
    }

    public void setShutdownHook(Thread shutdownHook) {
        this.shutdownHook = shutdownHook;
    }
}
