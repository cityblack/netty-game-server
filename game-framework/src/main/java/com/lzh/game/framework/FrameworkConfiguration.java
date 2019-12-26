package com.lzh.game.framework;

import com.lzh.game.framework.cmd.ParseCmdLoad;
import com.lzh.game.framework.cmd.DefaultCmdMappingManage;
import com.lzh.game.framework.scheduler.SchedulerOption;
import com.lzh.game.framework.scheduler.impl.SchedulerOptionImpl;
import com.lzh.game.socket.dispatcher.ExchangeProcess;
import com.lzh.game.socket.dispatcher.ServerExchange;
import com.lzh.game.socket.dispatcher.action.convent.InnerParamDataBindHandler;
import com.lzh.game.socket.dispatcher.mapping.CmdMappingManage;
import com.lzh.game.socket.exchange.Request;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Configuration
public class FrameworkConfiguration {

    @Bean
    public CmdMappingManage cmdMappingManage(ParseCmdLoad parseCmdLoad) {
        return new DefaultCmdMappingManage(parseCmdLoad);
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnMissingBean
    public ParseCmdLoad parseCmdLoad() {
        return () -> Collections.EMPTY_LIST;
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnMissingBean
    public InnerParamDataBindHandler innerParamDataBindHandler() {
        return new InnerParamDataBindHandler() {
            @Override
            public Object conventData(Request request, MethodParameter parameter) {
                return null;
            }

            @Override
            public boolean isInnerParam(MethodParameter parameter) {
                return false;
            }
        };
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnMissingBean
    public ExchangeProcess process() {
        return new ExchangeProcess() {
            @Override
            public <T> CompletableFuture<T> addRequestProcess(ServerExchange exchange, Function<ServerExchange, T> function) {
                return CompletableFuture.supplyAsync(() -> function.apply(exchange));
            }
        };
    }


    @Bean
    public SchedulerOption schedulerOption(Scheduler scheduler) {
        return new SchedulerOptionImpl(scheduler);
    }
}
