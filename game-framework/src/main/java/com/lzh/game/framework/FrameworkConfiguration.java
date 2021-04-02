package com.lzh.game.framework;

import com.lzh.game.common.CommonConfig;
import com.lzh.game.framework.scheduler.SchedulerOption;
import com.lzh.game.framework.scheduler.impl.SchedulerOptionImpl;
import com.lzh.game.repository.GameRepositoryBean;
import com.lzh.game.resource.GameResourceBean;
import com.lzh.game.socket.config.GameSocketConfiguration;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigureBefore(GameSocketConfiguration.class)
@Import({ GameResourceBean.class, GameRepositoryBean.class, CommonConfig.class })
public class FrameworkConfiguration {

    @Bean
    public SchedulerOption schedulerOption(Scheduler scheduler) {
        return new SchedulerOptionImpl(scheduler);
    }
}
