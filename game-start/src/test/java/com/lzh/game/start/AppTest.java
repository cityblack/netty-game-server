package com.lzh.game.start;

import com.lzh.game.framework.logs.anno.EnableLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zehong.l
 * @since 2024-10-08 16:51
 **/
@SpringBootApplication
@EnableLog("com.lzh.game.start")
public class AppTest {

    public static void main(String[] args) {
        SpringApplication.run(AppTest.class, args);
    }
}
