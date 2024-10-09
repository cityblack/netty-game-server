package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.starter.anno.EnableTcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zehong.l
 * @since 2024-09-27 14:23
 **/
@EnableTcpServer
@SpringBootApplication
public class TestApp {

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

}
