package com.lzh.game.framework.client.bootstrap;

import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.process.Process;

public class ResponseProcess implements Process<Response> {

    private ResponseDispatcher dispatcher;

    public ResponseProcess(ResponseDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void process(RemoteContext context, Response response) {
        response.setSession(context.getSession());
        dispatcher.doResponse(response);
    }
}
