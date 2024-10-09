package com.lzh.game.start.log;

import com.lzh.game.framework.logs.LogHandler;
import com.lzh.game.framework.logs.anno.LogFacade;
import com.lzh.game.framework.logs.param.LogReasonParam;
import com.lzh.game.start.AppTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zehong.l
 * @since 2024-10-08 16:46
 **/
@SpringBootTest(classes = AppTest.class)
class LogFileTest {

    @Test
    public void log() {
        LogHandler.getLog(Log.class).create(1, LogReason.CONSOLE);
    }

    @LogFacade
    public interface Log {

        @LogDesc(logFile = LogFile.ITEM)
        void create(int id, LogReasonParam param);
    }
}