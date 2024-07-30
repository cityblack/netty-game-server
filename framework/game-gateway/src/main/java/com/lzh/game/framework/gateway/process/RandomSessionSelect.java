package com.lzh.game.framework.gateway.process;

import com.lzh.game.framework.socket.core.bootstrap.client.AbstractClient;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomSessionSelect implements ForwardSessionSelect {

    private final AtomicInteger COUNT = new AtomicInteger(0);

    private final AbstractClient<?> client;

    private final CopyOnWriteArrayList<Session> sessions;

    public RandomSessionSelect(AbstractClient<?> client) {
        this.client = client;
        this.sessions = new CopyOnWriteArrayList<>();
        this.init();
    }

    private void init() {
        var manage = client.getContext().getSessionManage();
        manage.addCloseListening(sessions::remove);
        manage.addConnectListener(sessions::add);
    }

    @Override
    public Session selected(Request request) {
        if (sessions.isEmpty()) {
            return null;
        }
        int index = COUNT.incrementAndGet() % sessions.size();
        return sessions.get(index);
    }

}
