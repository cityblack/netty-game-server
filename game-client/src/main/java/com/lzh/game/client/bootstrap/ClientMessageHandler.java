package com.lzh.game.client.bootstrap;

import com.lzh.game.client.support.ExchangeProtocol;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.session.Session;

public class ClientMessageHandler implements MessageHandler {

    private ResponseDispatcher dispatcher;

    public ClientMessageHandler(ResponseDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void opened(Session session) {

    }

    @Override
    public void close(Session session) {

    }

    @Override
    public void exceptionCaught(Session session) {

    }

    @Override
    public void messageReceived(Session session, Object data) {
        dispatcher.doResponse(session.getChannel(), (ExchangeProtocol.Response) data);
    }
}
