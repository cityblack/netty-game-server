package com.lzh.game.socket.core.process;

import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.core.RemoteContext;
import com.lzh.game.socket.core.RequestFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureResponseProcess implements Process<GameResponse> {

    @Override
    public void process(RemoteContext context, GameResponse response) {
        response.setSession(context.getSession());
        RequestFuture.received(response, false);
    }
}
