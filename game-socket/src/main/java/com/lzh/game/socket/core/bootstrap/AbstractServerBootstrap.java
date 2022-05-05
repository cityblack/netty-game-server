package com.lzh.game.socket.core.bootstrap;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.GameServer;
import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.RequestHandler;
import com.lzh.game.socket.core.RequestProcess;
import com.lzh.game.socket.core.RequestProcessPool;
import com.lzh.game.socket.core.ServerExchange;
import com.lzh.game.socket.core.filter.Filter;
import com.lzh.game.socket.core.filter.FilterHandler;
import com.lzh.game.socket.core.invoke.*;
import com.lzh.game.socket.core.session.*;
import com.lzh.game.socket.core.session.cache.GameSessionMemoryCacheManage;
import com.lzh.game.socket.core.session.cache.SessionMemoryCacheManage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractServerBootstrap
        extends AbstractBootstrap<GameServerSocketProperties> implements GameServer {

    private NetServer netServer;

    private RequestActionSupport<EnhanceHandlerMethod> methodSupport;

    private InvokeMethodArgumentValues<Request> argumentValues;

    private RequestHandler handler;

    private ConvertManager convertManager;

    private List<Filter> filters = new ArrayList<>();

    private List<Object> beans = new ArrayList<>();

    public AbstractServerBootstrap(GameServerSocketProperties properties) {
        this(properties, defaultSession());
    }

    public AbstractServerBootstrap(GameServerSocketProperties properties, SessionManage<Session> sessionManage) {
        super(properties, sessionManage);
    }

    protected abstract NetServer createServer(int port);

    @Override
    protected void doInit(GameServerSocketProperties properties) {
        if (Objects.isNull(convertManager)) {
            convertManager = new DefaultConvertManager();
        }
        if (Objects.isNull(argumentValues)) {
            argumentValues = new InvokeMethodArgumentValuesImpl(convertManager);
        }
        if (Objects.isNull(methodSupport)) {
            methodSupport = new DefaultActionMethodSupport();
            if (!this.beans.isEmpty()) {
                for (Object bean : this.beans) {
                    this.addInvokeBean0(bean);
                }
            }
        }
        if (Objects.isNull(handler)) {
            handler = new FilterHandler(this.filters, new ActionRequestHandler(methodSupport, argumentValues));
        }
        if (Objects.isNull(getProcessManager().getProcess(Constant.REQUEST_COMMAND_KEY))) {
            addProcess(Constant.REQUEST_COMMAND_KEY, new RequestProcess(handler, createPool()));
        }
        this.netServer = createServer(getPort());
    }

    private static SessionManage<Session> defaultSession() {
        SessionMemoryCacheManage<String, Session> cacheManage = new GameSessionMemoryCacheManage<>();
        SessionFactory<Session> factory = ServerGameSession::of;
        return new GameSessionManage<>(cacheManage, factory);
    }

    @Override
    public void asyncStart() {
        this.netServer.asyncStart();
    }

    @Override
    protected void startup() {
        this.netServer.start();
    }

    @Override
    public void shutDown() {
        super.shutDown();
        this.netServer.shutDown();
    }

    @Override
    public int getPort() {
        return this.properties.getPort();
    }

    public void addInvokeBean(Object bean) {
        if (isStared()) {
            this.addInvokeBean0(bean);
        } else {
            this.beans.add(bean);
        }
    }

    private void addInvokeBean0(Object bean) {
        List<InvokeUtils.InvokeModel> list = InvokeUtils.parseBean(bean, convertManager.inner());
        for (InvokeUtils.InvokeModel model : list) {
            if (model.hasResponse()) {
                methodSupport.register(model.getValue(), model.getHandlerMethod(), model.getResponse());
            } else {
                methodSupport.register(model.getValue(), model.getHandlerMethod());
            }
            if (model.hasParam()) {
                convertManager.registerConvert(model.getParamClass(), new ProtoBufferConvert<>(model.getParamClass()));
            }
        }
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    // === set ====
    public AbstractServerBootstrap setMethodSupport(RequestActionSupport<EnhanceHandlerMethod> methodSupport) {
        this.methodSupport = methodSupport;
        return this;
    }

    public AbstractServerBootstrap setArgumentValues(InvokeMethodArgumentValues<Request> argumentValues) {
        this.argumentValues = argumentValues;
        return this;
    }

    public AbstractServerBootstrap setConvertManager(ConvertManager convertManager) {
        this.convertManager = convertManager;
        return this;
    }

    public AbstractServerBootstrap setHandler(RequestHandler handler) {
        this.handler = handler;
        return this;
    }

    private RequestProcessPool createPool() {
        return new RequestProcessPool() {

            private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            @Override
            public void submit(ServerExchange exchange, Runnable runnable) {
                service.submit(runnable);
            }

            @Override
            public void submit(Session session, Runnable runnable) {
                service.submit(runnable);
            }
        };
    }

}
