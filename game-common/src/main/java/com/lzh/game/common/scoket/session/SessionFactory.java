package com.lzh.game.common.scoket.session;

import io.netty.channel.Channel;

@FunctionalInterface
public interface SessionFactory<T extends Session> {

    T createSession(Channel channel);
}
