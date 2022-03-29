package com.lzh.game.socket.core.session;

import com.lzh.game.common.scoket.session.AbstractSession;
import com.lzh.game.common.scoket.session.Session;
import io.netty.channel.Channel;

import java.io.Serializable;

public class ServerGameSession extends AbstractSession
        implements Session, Serializable {

    private static final long serialVersionUID = 8359226998972303381L;

    protected ServerGameSession(Channel channel) {
        super(channel);
    }

    public static ServerGameSession of(Channel channel) {
        return new ServerGameSession(channel);
    }

}
