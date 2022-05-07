package com.lzh.game.client.bootstrap;

import com.lzh.game.socket.GameResponse;
import com.lzh.game.socket.core.process.Process;
import com.lzh.game.socket.core.RemoteContext;

public class ResponseProcess implements Process<GameResponse> {

    private ResponseDispatcher dispatcher;

    public ResponseProcess(ResponseDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void process(RemoteContext context, GameResponse gameResponse) {
        gameResponse.setSession(context.getSession());
        dispatcher.doResponse(gameResponse);
    }
}
