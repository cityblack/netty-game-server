package com.lzh.game.framework.logs;

import com.lzh.game.framework.logs.anno.EnableLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zehong.l
 * @date 2024-04-02 12:33
 **/
@SpringBootApplication
@EnableLog({"com.lzh.game.framework"})
public class AppTest {

    public static void main(String[] args) {
        SpringApplication.run(AppTest.class,args);
    }
}
