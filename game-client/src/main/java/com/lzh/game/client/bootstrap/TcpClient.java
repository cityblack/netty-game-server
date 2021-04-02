package com.lzh.game.client.bootstrap;

import io.netty.channel.Channel;

public interface TcpClient {

    void conn(String host, int port);

    Channel getChannel();
}
