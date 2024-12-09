package com.lzh.game.framework.socket.starter.config;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerBootstrap;
import com.lzh.game.framework.socket.core.invoke.convert.InvokeMethodArgumentValues;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.session.SessionManage;
import com.lzh.game.framework.socket.starter.server.SpringServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author zehong.l
 * @since 2024-10-17 17:57
 **/
public abstract class ServerRegistry implements FactoryBean<SpringServer>, BeanFactoryAware {

    BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public SpringServer<SpringGameServerProperties> getObject() throws Exception {
        var context = createContext();
        var tcpServer = createServer(context);
        var server = new SpringServer<>(tcpServer);
        server.asyncStart();
        return server;
    }

    protected abstract ServerBootstrap<SpringGameServerProperties> createServer(BootstrapContext<SpringGameServerProperties> context);

    @Override
    public Class<?> getObjectType() {
        return SpringServer.class;
    }


    @SuppressWarnings("unchecked")
    private BootstrapContext<SpringGameServerProperties> createContext() {
        var properties = beanFactory.getBean(SpringGameServerProperties.class);
        var messageManager = beanFactory.getBean(MessageManager.class);
        var session = beanFactory.getBean(SessionManage.class);
        var invokeSupport = beanFactory.getBean(InvokeSupport.class);
        var values = beanFactory.getBean(InvokeMethodArgumentValues.class);
        return BootstrapContext.of(properties, session, messageManager, invokeSupport, values);
    }
}