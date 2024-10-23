package com.lzh.game.framework.socket.core.session.impl;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.session.Session;
import io.netty.channel.Channel;

import java.io.Serializable;

public class GameSession extends AbstractSession
        implements Session, Serializable {

    private static final long serialVersionUID = 8359226998972303381L;

    protected GameSession(Channel channel, BootstrapContext<?> context) {
        super(channel, context);
    }

    public static GameSession of(Channel channel, BootstrapContext<?> context) {
        return new GameSession(channel, context);
    }

}
