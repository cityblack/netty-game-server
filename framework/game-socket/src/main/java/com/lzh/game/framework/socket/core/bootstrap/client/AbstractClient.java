package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.AbstractBootstrap;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.core.session.monitor.ConnectMonitor;
import com.lzh.game.framework.socket.core.session.monitor.DefaultConnectMonitor;

/**
 * @author zehong.l
 * @since 2024-07-22 17:40
 **/
public abstract class AbstractClient<C extends GameClientSocketProperties>
        extends AbstractBootstrap<C> implements GameClient {

    private ConnectMonitor monitor;

    public AbstractClient(C properties, SessionManage<Session> sessionManage, MessageManager messageManager, InvokeSupport invokeSupport) {
        super(properties, sessionManage, messageManager, invokeSupport);
    }

    public AbstractClient(C properties, SessionManage<Session> sessionManage) {
        super(properties, sessionManage);
    }

    public AbstractClient(C properties) {
        super(properties);
    }


    @Override
    protected void init() {
        super.init();
        this.monitor = new DefaultConnectMonitor(getSessionManage());
    }

    public ConnectMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ConnectMonitor monitor) {
        this.monitor = monitor;
    }
}
