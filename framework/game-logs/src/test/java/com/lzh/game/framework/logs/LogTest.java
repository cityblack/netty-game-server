package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.LogFacade;

/**
 * @author zehong.l
 * @date 2024-04-02 12:13
 **/
@LogFacade
public interface LogTest {

    @LogMethod(logFile = "helloWorld", logReason = 1000)
    void logTest(int hello, String world);
}
