package com.lzh.game.framework.socket.starter;

import com.lzh.game.framework.socket.GameServerSocketProperties;
import com.lzh.game.framework.socket.core.bootstrap.TcpCommonServer;
import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpringTcpCommonServer extends TcpCommonServer implements ApplicationContextAware {

    private List<SocketServerInit> inits;

    protected SpringTcpCommonServer(GameServerSocketProperties properties, SessionManage<? extends Session> sessionManage, ProcessorPipeline pipeline, MessageManager messageManager) {
        super(properties, sessionManage, pipeline, messageManager);
    }

    protected SpringTcpCommonServer(GameServerSocketProperties properties, SessionManage<? extends Session> sessionManage) {
        super(properties, sessionManage);
    }

    protected SpringTcpCommonServer(GameServerSocketProperties properties) {
        super(properties);
    }

    @Override
    protected void doInit(GameServerSocketProperties properties) {
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
