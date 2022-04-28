package com.lzh.game.client;


import com.lzh.game.common.scoket.GameRequest;
import com.lzh.game.common.scoket.Request;
import io.netty.channel.Channel;

public class PackUtils {

    public static void sendMessage(Channel channel, int protocol, Object data) {
        GameRequest request = new GameRequest();
        request.setCmd(protocol);
        channel.writeAndFlush(request);
    }

}
