package com.lzh.game.framework.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.lzh.game.framework.repository",
})
@EnableMongoRepositories
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
