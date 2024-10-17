package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.core.bootstrap.server.GameServer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zehong.l
 * @since 2024-10-17 16:49
 **/
@SpringBootTest(classes = TestApp.class)
public class ServerTest {

    @Resource
    private GameServer server;

    @Test
    public void port() {
        System.out.println(server.getPort());
    }
}
