package com.lzh.game.client;

import com.lzh.game.socket.SocketUtils;
import io.netty.channel.Channel;

public class PackUtils {

    public static void sendMessage(Channel channel, int protocol, Object data) {
        channel.writeAndFlush(SocketUtils.createCommonRequest(protocol, data));
    }
}
