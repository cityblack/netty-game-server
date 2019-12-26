package com.lzh.game.start;

import com.lzh.game.socket.GameServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.lzh.game.common",
        "com.lzh.game.socket",
        "com.lzh.game.framework",
        "com.lzh.game.resource",
        "com.lzh.game.repository",
        "com.lzh.game.start",
})
@EnableMongoRepositories
public class App implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(GameServer.class).start();
    }
}
