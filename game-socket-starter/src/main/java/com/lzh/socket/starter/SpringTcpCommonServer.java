package com.lzh.socket.starter;

import com.lzh.game.socket.GameServerSocketProperties;
import com.lzh.game.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpringTcpCommonServer extends TcpCommonServer implements ApplicationContextAware {

    private List<SocketServerInit> inits;

    public SpringTcpCommonServer(GameServerSocketProperties properties) {
        super(properties);
    }

    public SpringTcpCommonServer(GameServerSocketProperties properties, SessionManage<Session> sessionManage) {
        super(properties, sessionManage);
    }

    @Override
    protected void doInit(GameServerSocketProperties properties) {
        if (Objects.nonNull(inits)) {
            for (SocketServerInit init : this.inits) {
                init.doInit(properties, this);
            }
        }
        super.doInit(properties);
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
