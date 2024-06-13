package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.RequestFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureResponseProcess implements Processor<Response> {

    @Override
    public void process(Response response) {
        RequestFuture.received(response, false);
    }
}
