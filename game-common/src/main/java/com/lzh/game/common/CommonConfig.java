package com.lzh.game.common;

import com.lzh.game.common.event.EventBusAutoRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public EventBusAutoRegister eventBusAutoRegister() {
        return new EventBusAutoRegister();
    }

}
