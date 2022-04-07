package com.lzh.game.client;


import com.google.protobuf.ByteString;
import com.lzh.game.client.support.ExchangeProtocol;
import com.lzh.game.common.scoket.GameRequest;
import com.lzh.game.common.scoket.Request;
import com.lzh.game.common.serialization.ProtoBufUtils;
import io.netty.channel.Channel;

import java.util.Objects;

public class PackUtils {

    public static void sendMessage(Channel channel, int protocol, Object data) {
        GameRequest request = new GameRequest();
        request.setCmd(protocol);
        channel.writeAndFlush(request);
    }

}
