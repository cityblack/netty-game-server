package com.lzh.netty.socket;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args) throws ClassNotFoundException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("game.xml");
        applicationContext.start();
    }
}
