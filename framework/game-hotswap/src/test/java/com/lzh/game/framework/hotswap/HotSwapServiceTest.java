package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.agent.HotSwapBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author zehong.l
 * @since 2024-08-05 15:07
 **/
class HotSwapServiceTest {

    @Test
    void execute() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        var bean = new HotSwapBean();

//        bean.starVM();
//        System.out.println("after:" + HotSwapBean.update);
    }
}