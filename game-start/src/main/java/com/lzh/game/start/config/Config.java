package com.lzh.game.start.config;

import com.lzh.game.framework.logs.desc.LogDescDefined;
import com.lzh.game.framework.logs.desc.LogDescHandler;
import com.lzh.game.framework.socket.core.invoke.convert.DefaultInvokeMethodArgumentValues;
import com.lzh.game.start.StartProperties;
import com.lzh.game.start.config.convert.PlayerConvert;
import com.lzh.game.start.log.LogDesc;
import com.lzh.game.start.pool.DefaultBusinessThreadExecutorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@EnableConfigurationProperties(StartProperties.class)
public class Config {

    @Bean(name = "requestBusinessPool")
    public DefaultBusinessThreadExecutorService exchangeProcess() {
        return DefaultBusinessThreadExecutorService.getInstance();
    }

    @Bean
    public LogDescHandler logDescHandler() {
        return method -> {
            var desc = method.getAnnotation(LogDesc.class);
            if (Objects.isNull(desc)) {
                return null;
            }
            return new LogDescDefined(desc.logFile().name(), desc.logReason().getId());
        };
    }

    @Bean
    public DefaultInvokeMethodArgumentValues argumentValues() {
        var bean = new DefaultInvokeMethodArgumentValues();
        bean.registerConvert(new PlayerConvert());
        return bean;
    }
}
