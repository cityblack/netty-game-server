package com.lzh.game.start.model.hello;

import com.lzh.game.socket.annotation.Action;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.start.cmd.CmdMessage;
import lombok.extern.slf4j.Slf4j;

@Action
@Slf4j
public class HelloAction {

    @RequestMapping(CmdMessage.CM_HELLO)
    @ResponseMapping(CmdMessage.SM_HELLO)
    public ResponseHello hello(Session session, RequestHello hello) {
        log.info("Get session:{} info: {}", session.getId(), hello);
        return new ResponseHello("I am server.", 10001);
    }
}
