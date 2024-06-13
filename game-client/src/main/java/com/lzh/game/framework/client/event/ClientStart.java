package com.lzh.game.framework.client.event;

import com.lzh.game.framework.socket.core.bootstrap.GameClient;
import com.lzh.game.common.event.Event;
import lombok.Getter;

public class ClientStart implements Event {

    @Getter
    private GameClient client;

    public static ClientStart of(GameClient client) {
        ClientStart start = new ClientStart();
        start.client = client;
        return start;
    }

    @Override
    public GameClient getOwn() {
        return client;
    }
}
