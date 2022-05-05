package com.lzh.game.start.demo;

import com.lzh.game.client.model.hello.RequestHello;
import com.lzh.game.client.model.hello.ResponseHello;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.socket.starter.Action;
import lombok.extern.slf4j.Slf4j;

@Action
@Slf4j
public class HelloAction {

    @RequestMapping(CmdMessage.CM_HELLO)
    @ResponseMapping(CmdMessage.SM_HELLO)
    public ResponseHello hello(RequestHello hello) {
        log.info("Server get the message:{}", hello.getContent());
        ResponseHello response = new ResponseHello();
        response.setContent("Server got");
        return response;
    }
}
