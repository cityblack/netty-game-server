package com.lzh.game.socket.exchange.session;

import io.netty.channel.Channel;

public interface ChannelSessionManage {

    Session createSession(Channel channel);

    Session getSession(Channel channel);

    Session closeSession(Channel channel);

    Channel getChannelBySession(Session session);
}
