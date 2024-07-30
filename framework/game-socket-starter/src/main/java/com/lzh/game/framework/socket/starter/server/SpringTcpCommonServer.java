package com.lzh.game.framework.socket.starter.server;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.TcpCommonServer;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.starter.SocketServerInit;
import com.lzh.game.framework.socket.starter.config.SpringGameServerProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpringTcpCommonServer extends TcpCommonServer<SpringGameServerProperties>
        implements ApplicationContextAware {

    private List<SocketServerInit> inits;

    public SpringTcpCommonServer(SpringGameServerProperties properties, BootstrapContext context) {
        super(properties, context);
    }

    public SpringTcpCommonServer(SpringGameServerProperties properties) {
        super(properties);
    }


    @Override
    protected void doInit(SpringGameServerProperties properties) {
        super.doInit(properties);
        if (Objects.nonNull(inits)) {
            for (SocketServerInit init : this.inits) {
                init.doInit(properties, this);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, SocketServerInit> beans = context.getBeansOfType(SocketServerInit.class);
        List<SocketServerInit> list = new ArrayList<>(beans.size());
        for (Map.Entry<String, SocketServerInit> entry : beans.entrySet()) {
            list.add(entry.getValue());
        }
        this.inits = list;
    }
}
