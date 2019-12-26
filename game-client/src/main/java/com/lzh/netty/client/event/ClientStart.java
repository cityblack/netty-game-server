package com.lzh.netty.client.event;

import com.lzh.game.common.event.Event;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClientStart implements Event {

    @Getter
    private Channel channel;

    @Override
    public Channel getOwn() {
        return null;
    }

    public static ClientStart of(Channel channel) {
        ClientStart start = new ClientStart();
        start.channel = channel;
        return start;
    }
}
