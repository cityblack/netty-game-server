package com.lzh.game.framework.socket.core.session;

import io.netty.channel.Channel;

@FunctionalInterface
public interface SessionFactory<T extends Session> {

    T createSession(Channel channel);
}
