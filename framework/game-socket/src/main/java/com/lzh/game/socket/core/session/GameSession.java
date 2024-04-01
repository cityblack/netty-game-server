package com.lzh.game.socket.core.session;

import io.netty.channel.Channel;

import java.io.Serializable;

public class GameSession extends AbstractSession
        implements Session, Serializable {

    private static final long serialVersionUID = 8359226998972303381L;

    protected GameSession(Channel channel) {
        super(channel);
    }

    public static GameSession of(Channel channel) {
        return new GameSession(channel);
    }

}
