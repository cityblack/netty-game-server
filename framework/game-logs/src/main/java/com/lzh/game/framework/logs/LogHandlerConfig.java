package com.lzh.game.framework.logs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zehong.l
 * @date 2024-04-01 18:14
 **/
@Configuration
@EnableConfigurationProperties
public class LogHandlerConfig implements CommandLineRunner {

    @Value("${game.logs.scan.path:com.lzh.game}")
    private String scanPath;

    @Override
    public void run(String... args) throws Exception {
        LogHandler.init(scanPath);
    }
}
