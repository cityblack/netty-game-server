package com.lzh.game.framework.socket.core.session;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import io.netty.channel.Channel;

@FunctionalInterface
public interface SessionFactory<T extends Session> {

    T createSession(Channel channel, BootstrapContext<?> context);
}
