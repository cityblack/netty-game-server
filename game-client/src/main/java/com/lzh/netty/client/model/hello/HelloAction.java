package com.lzh.netty.client.model.hello;

import com.lzh.netty.client.Action;
import com.lzh.netty.client.support.Response;
import lombok.extern.slf4j.Slf4j;

@Action
@Slf4j
public class HelloAction {

    @Response(-10087)
    public void hello(ResponseHello hello) {
        log.info("Get info: {}", hello);
    }
}
