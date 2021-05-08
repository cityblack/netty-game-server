package com.lzh.game.start;

import com.lzh.game.framework.config.EnableTcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableTcpServer
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
