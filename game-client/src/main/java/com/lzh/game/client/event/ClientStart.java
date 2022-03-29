package com.lzh.game.client.event;

import com.lzh.game.client.bootstrap.TcpClient;
import com.lzh.game.common.event.Event;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClientStart implements Event {

    @Getter
    private TcpClient client;

    public static ClientStart of(TcpClient client) {
        ClientStart start = new ClientStart();
        start.client = client;
        return start;
    }

    @Override
    public TcpClient getOwn() {
        return client;
    }
}
