package com.lzh.netty.socket.protocol.session;

import io.netty.channel.Channel;

public interface ChannelSession {

    Session createSession(Channel channel);

    Session getSession(Channel channel);

    Session closeSession(Channel channel);
}
