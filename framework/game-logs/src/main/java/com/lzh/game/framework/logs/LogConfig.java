package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.DefaultLogDesc;
import com.lzh.game.framework.logs.desc.LogDescDefined;
import com.lzh.game.framework.logs.desc.LogDescHandler;
import com.lzh.game.framework.logs.invoke.DefaultLogInvoke;
import com.lzh.game.framework.logs.invoke.LogInvoke;
import com.lzh.game.framework.logs.invoke.serializer.FastJson2ContentSerializer;
import com.lzh.game.framework.logs.invoke.serializer.LogContentSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-07-12 18:30
 **/
@Configuration
public class LogConfig {

    @Bean
    @ConditionalOnMissingBean
    public LogInvoke logInvoke(LogContentSerializer logContentSerializer) {
        return new DefaultLogInvoke(logContentSerializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogContentSerializer logContentSerializer() {
        return new FastJson2ContentSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogDescHandler logDescHandler() {
        return method -> {
            var desc = method.getAnnotation(DefaultLogDesc.class);
            if (Objects.isNull(desc)) {
                return null;
            }
            return new LogDescDefined(desc.logFile(), desc.logReason());
        };
    }
}
