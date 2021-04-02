package com.lzh.game.client.model.hello;

import com.lzh.game.client.support.Response;
import com.lzh.game.client.Action;
import lombok.extern.slf4j.Slf4j;

@Action
@Slf4j
public class HelloAction {

    @Response(-10087)
    public void hello(ResponseHello hello) {
        log.info("Get info: {}", hello);
    }
}
