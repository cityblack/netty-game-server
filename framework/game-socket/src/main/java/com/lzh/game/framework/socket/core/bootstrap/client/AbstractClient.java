package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.session.monitor.ConnectMonitor;
import com.lzh.game.framework.socket.core.session.monitor.DefaultConnectMonitor;

import java.util.concurrent.ExecutorService;

/**
 * @author zehong.l
 * @since 2024-07-22 17:40
 **/
public abstract class AbstractClient<C extends GameClientSocketProperties>
        extends AbstractBootstrap<C> implements GameClient {

    private ConnectMonitor monitor;

    private ExecutorService requestService;

    public AbstractClient(C properties, BootstrapContext context) {
        super(properties, context);
    }

    public AbstractClient(C properties) {
        super(properties);
    }

    @Override
    protected void init() {
        super.init();
        this.monitor = new DefaultConnectMonitor(getContext().getSessionManage());
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
}
