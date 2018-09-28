package com.lzh.netty.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.lzh.netty.socket",
        "com.lzh.netty.framework",
        "com.lzh.netty.start",
})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }

}
