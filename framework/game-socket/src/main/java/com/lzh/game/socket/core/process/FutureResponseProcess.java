package com.lzh.game.socket.core.process;

import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.RequestFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureResponseProcess implements Process<Response> {

    @Override
    public void process(RemoteContext context, Response response) {
        response.setSession(context.getSession());
        RequestFuture.received(response, false);
    }
}
