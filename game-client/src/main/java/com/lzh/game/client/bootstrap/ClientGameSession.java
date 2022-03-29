package com.lzh.game.client.bootstrap;

import com.lzh.game.common.scoket.session.AbstractSession;
import com.lzh.game.common.scoket.session.Session;
import io.netty.channel.Channel;

public class ClientGameSession extends AbstractSession implements Session {

    protected ClientGameSession(Channel channel) {
        super(channel);
    }

    public static ClientGameSession of(Channel channel) {
        return new ClientGameSession(channel);
    }
}
