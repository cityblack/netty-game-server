package com.lzh.game.framework.client.bootstrap;

import com.lzh.game.socket.core.protocol.Response;
import com.lzh.game.socket.core.process.Processor;

public class ResponseProcess implements Processor<Response> {

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
