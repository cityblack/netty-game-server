package com.lzh.game.framework.gateway;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.ForwardSessionSelect;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.Session;

import java.util.List;
import java.util.Random;

public class RandomSessionSelect implements ForwardSessionSelect {

    private Random random = new Random();

    @Override

    public Session selected(GameTcpClient client, Request request) {
        List<Session> sessions = client.getSessionManage().getAllSession();
        if (sessions.isEmpty()) {
            return null;
        }
        int index = random(sessions.size());
        return sessions.get(index);
    }

    private int random(int max) {
        return random.nextInt(max + 1);
    }
}
