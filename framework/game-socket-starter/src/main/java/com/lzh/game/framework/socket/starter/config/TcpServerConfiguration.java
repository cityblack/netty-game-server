package com.lzh.game.framework.socket.starter.config;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.server.ServerBootstrap;
import com.lzh.game.framework.socket.core.bootstrap.tcp.TcpServer;
import com.lzh.game.framework.socket.starter.server.SpringServer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class TcpServerConfiguration implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        var definition = new RootBeanDefinition();
        definition.setTargetType(SpringServer.class);
        definition.setBeanClass(TcpServerRegistry.class);
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(SpringServer.class.getName(), definition);
    }

    public static class TcpServerRegistry extends ServerRegistry {

        @Override
        protected ServerBootstrap<SpringGameServerProperties> createServer(BootstrapContext<SpringGameServerProperties> context) {
            return new TcpServer<>(context);
        }
    }
}
