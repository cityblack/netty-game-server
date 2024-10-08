package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.agent.ClassFileInfo;
import com.lzh.game.framework.hotswap.agent.HotSwapBean;
import com.lzh.game.framework.hotswap.handler.AbstractFixHandler;
import com.lzh.game.framework.hotswap.handler.dao.FixDao;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-08-02 14:49
 **/
@Slf4j
public class HotSwapService {

    private final String agentLibPath;

    private final FixDao fixBugDao;

    public HotSwapService(FixDao fixBugDao, String agentLibPath) {
        this.fixBugDao = fixBugDao;
        this.agentLibPath = agentLibPath;
        if (!agentLibPath.endsWith(".jar")) {
            throw new IllegalArgumentException("Hot swap lib path error:" + agentLibPath);
        }
    }

    public synchronized void execute(String[] dir) {
        log.info("Start Hotting swap.");
        if (Objects.isNull(dir) || dir.length == 0) {
            log.error("Swap path is null...");
            return;
        }
        var classes = HotSwapBean.getInstance().swap(dir, agentLibPath);
        AbstractFixHandler.executeFix(classes.stream().map(ClassFileInfo::getClasName)
                .toList(), fixBugDao);
    }
}
