package com.lzh.game.framework.gateway.process;

import com.lzh.game.framework.socket.core.bootstrap.client.GameClientSocketProperties;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.session.Session;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSessionSelect implements ForwardSessionSelect {

    @Override
    public Session selected(GameTcpClient<GameClientSocketProperties> client, Request request) {
        List<Session> sessions = client.getSessionManage().getAllSession();
        if (sessions.isEmpty()) {
            return null;
        }
        int index = random(sessions.size());
        return sessions.get(index);
    }

    private int random(int max) {
        return ThreadLocalRandom.current().nextInt(max + 1);
    }
}