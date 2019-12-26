package com.lzh.netty.client.bootstrap;

import io.netty.channel.Channel;

public interface TcpClient {

    void conn(String host, int port);

    Channel getChannel();
}
