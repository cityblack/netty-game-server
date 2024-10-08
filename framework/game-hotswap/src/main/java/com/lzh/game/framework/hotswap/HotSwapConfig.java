package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.handler.dao.FixDao;
import com.lzh.game.framework.hotswap.handler.dao.LocalFileFixDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;

/**
 * @author zehong.l
 * @since 2024-10-08 14:53
 **/
@Configuration
public class HotSwapConfig {

    @Value("${game.hot-swap.agent-lib:hot_agent.jar}")
    private String agentLibPath;

    @Value(("${game.hot-swap.local-log.path:hot_agent_log}"))
    private String localFileLogPath;

    @Bean
    @ConditionalOnMissingBean
    public FixDao fixDao() {
        return new LocalFileFixDao(localFileLogPath);
    }

    @Bean
    public HotSwapService hotSwapService(FixDao fixDao) {
        return new HotSwapService(fixDao, agentLibPath);
    }
}
