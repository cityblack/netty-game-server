package com.lzh.game.client.bootstrap;

import com.lzh.game.socket.core.session.AbstractSession;
import com.lzh.game.socket.core.session.Session;
import io.netty.channel.Channel;

public class ClientGameSession extends AbstractSession implements Session {

    protected ClientGameSession(Channel channel) {
        super(channel);
    }

    public static ClientGameSession of(Channel channel) {
        return new ClientGameSession(channel);
    }
}
