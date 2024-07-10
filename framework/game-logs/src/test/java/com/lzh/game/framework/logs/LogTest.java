package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.LogFacade;
import com.lzh.game.framework.logs.anno.DefaultLogDesc;
import com.lzh.game.framework.logs.param.LogParam;

/**
 * @author zehong.l
 * @date 2024-04-02 12:13
 **/
@LogFacade
public interface LogTest {

    @DefaultLogDesc(logFile = "helloWorld", logReason = 1000)
    void logTest(int hello, String world);

    @DefaultLogDesc(logFile = "helloWorld")
    void test2(int hello, LogParam user);
}
