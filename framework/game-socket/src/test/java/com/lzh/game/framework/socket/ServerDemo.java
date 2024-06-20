package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.annotation.Receive;

public class ServerDemo {

    @Receive(-10086)
    public String hello(String hello) {
        System.out.println(hello);
        return "server say:" + hello;
    }

    @Receive(-10089)
    public void noeWay(String one) {
        System.out.println("one way:" + one);
    }
}
