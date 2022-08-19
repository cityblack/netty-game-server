package com.lzh.game.socket;

import com.lzh.game.socket.annotation.RequestMapping;

public class ServerDemo {

    @RequestMapping(-10086)
    public String hello(String hello) {
        System.out.println(hello);
        return "server say:" + hello;
    }

    @RequestMapping(-10089)
    public void noeWay(String one) {
        System.out.println("one way:" + one);
    }
}
