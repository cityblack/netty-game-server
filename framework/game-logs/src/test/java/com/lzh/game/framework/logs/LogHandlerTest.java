package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.EnableLog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zehong.l
 * @date 2023-05-31 15:42
 **/
@Slf4j
@SpringBootTest(classes = {com.lzh.game.framework.logs.AppTest.class})
public class LogHandlerTest {

    @Resource
    private LogTest logTest;

    @Test
    public void log() {

        logTest.logTest(1, "world");
        logTest.test2(2, name -> "lzh", () -> 1001);
    }

}
