package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.agent.HotSwapBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zehong.l
 * @since 2024-08-02 14:49
 **/
@Component
public class HotSwapService {

    @Value("game.hot-swap.agent-lib")
    private String agentLibPath;

    public void execute(String dir) throws Exception {
        var classes = HotSwapBean.getInstance().swap(dir, agentLibPath);

    }
}
