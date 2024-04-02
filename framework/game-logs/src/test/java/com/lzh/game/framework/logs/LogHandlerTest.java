package com.lzh.game.framework.logs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zehong.l
 * @date 2023-05-31 15:42
 **/
//@Slf4j
@SpringBootTest(classes = {com.lzh.game.framework.logs.AppTest.class})
public class LogHandlerTest {

    @Test
    public void log() {
        LogHandler.getLog(LogTest.class).logTest(10086, "hello world");
    }

    @Test
    public void t() {
        System.out.println(LoggerUtils.LogBuild.class.getName());
    }
}
