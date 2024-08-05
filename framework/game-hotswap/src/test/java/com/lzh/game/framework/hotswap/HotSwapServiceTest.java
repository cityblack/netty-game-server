package com.lzh.game.framework.hotswap;

import com.lzh.game.framework.hotswap.agent.HotSwapAgent;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import static org.junit.jupiter.api.Assertions.*;

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