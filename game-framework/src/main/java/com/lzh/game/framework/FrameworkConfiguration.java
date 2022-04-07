package com.lzh.game.framework;

import com.lzh.game.common.CommonConfig;
import com.lzh.game.framework.cmd.CmdMappingManage;
import com.lzh.game.framework.cmd.CmdParseFactory;
import com.lzh.game.framework.cmd.DefaultCmdMappingManage;
import com.lzh.game.framework.scheduler.SchedulerOption;
import com.lzh.game.framework.scheduler.impl.SchedulerOptionImpl;
import com.lzh.game.repository.GameRepositoryBean;
import com.lzh.game.resource.config.GameResourceBean;
import com.lzh.socket.starter.GameSocketConfiguration;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Configuration
@AutoConfigureBefore(GameSocketConfiguration.class)
@Import({ GameResourceBean.class, GameRepositoryBean.class, CommonConfig.class })
public class FrameworkConfiguration {

    @Bean
    public SchedulerOption schedulerOption(Scheduler scheduler) {
        return new SchedulerOptionImpl(scheduler);
    }

    @Bean
    public CmdMappingManage cmdMappingManage(CmdParseFactory cmdParseFactory) {
        return new DefaultCmdMappingManage(cmdParseFactory);
    }

    @Bean
    @ConditionalOnMissingClass
    @ConditionalOnMissingBean
    public CmdParseFactory parseCmdLoad() {
        return () -> Collections.EMPTY_LIST;
    }
}
