package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.handler.DefaultHeartbeatHandler;
import com.lzh.game.framework.socket.core.bootstrap.handler.HeartbeatHandler;
import com.lzh.game.framework.socket.core.session.monitor.ConnectMonitor;
import com.lzh.game.framework.socket.core.session.monitor.DefaultConnectMonitor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * @author zehong.l
 * @since 2024-07-22 17:40
 **/
public abstract class AbstractClient<C extends GameClientSocketProperties>
        extends AbstractBootstrap<C> implements GameClient {

    private ConnectMonitor monitor;

    private ExecutorService requestService;

    private HeartbeatHandler heartbeatHandler;

    public AbstractClient(BootstrapContext<C> context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        this.monitor = new DefaultConnectMonitor(getContext().getSessionManage());
        if (Objects.isNull(heartbeatHandler)) {
            this.heartbeatHandler = new DefaultHeartbeatHandler(this);
        }
        this.getHeartbeatHandler().start();
    }

    public ConnectMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ConnectMonitor monitor) {
        this.monitor = monitor;
    }

    public ExecutorService getRequestService() {
        return requestService;
    }

    public void setRequestService(ExecutorService requestService) {
        this.requestService = requestService;
    }

    public HeartbeatHandler getHeartbeatHandler() {
        return heartbeatHandler;
    }

    public void setHeartbeatHandler(HeartbeatHandler heartbeatHandler) {
        this.heartbeatHandler = heartbeatHandler;
    }

    @Override
    public void shutDown() {
        super.shutDown();
        this.heartbeatHandler.shutdown();
    }
}
