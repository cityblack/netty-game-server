package com.lzh.game.client.bootstrap;

import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.Response;
import com.lzh.game.common.scoket.session.Session;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

public class ClientMessageHandler implements MessageHandler {

    private ResponseDispatcher dispatcher;

    public ClientMessageHandler(ResponseDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void opened(Session session) {
        Channel channel = session.getChannel();
        if (!channel.attr(GameClientBootstrap.SESSION_FUTURE).compareAndSet(null, CompletableFuture.completedFuture(session))) {
            channel.attr(GameClientBootstrap.SESSION_FUTURE).get().complete(session);
        }
    }

    @Override
    public void close(Session session) {

    }

    @Override
    public void exceptionCaught(Session session) {

    }

    @Override
    public void messageReceived(Session session, Object data) {
        dispatcher.doResponse(session.getChannel(), (Response) data);
    }
}
