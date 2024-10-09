package com.lzh.game.start.demo;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.starter.anno.Action;
import lombok.extern.slf4j.Slf4j;

@Action
@Slf4j
public class HelloAction {

    @Receive
    public ResponseHello hello(RequestHello hello) {
        log.info("Server get the message:{}", hello.getContent());
        ResponseHello response = new ResponseHello();
        response.setContent("Server got");
        return response;
    }
}
