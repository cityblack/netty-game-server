package com.lzh.game.framework.gateway.config;

import com.lzh.game.framework.gateway.GateWay;
import com.lzh.game.framework.gateway.select.factory.RandomSessionSelectFactory;
import com.lzh.game.framework.gateway.select.factory.SessionSelectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GateConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "gatewaySelectFactory")
    public SessionSelectFactory selectFactory() {
        return new RandomSessionSelectFactory();
    }

    @Bean
    public GateWay gateWay(GatewayProperties properties) {
        return new GateWay(properties, selectFactory());
    }
}
