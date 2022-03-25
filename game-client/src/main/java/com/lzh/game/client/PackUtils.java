package com.lzh.game.client;


import com.google.protobuf.ByteString;
import com.lzh.game.client.support.ExchangeProtocol;
import com.lzh.game.common.serialization.ProtoBufUtils;
import io.netty.channel.Channel;

import java.util.Objects;

public class PackUtils {

    public static void sendMessage(Channel channel, int protocol, Object data) {
        ExchangeProtocol.Request request = buildRequest(protocol, data);
        channel.writeAndFlush(request);
    }

    private static ExchangeProtocol.Request buildRequest(int protocol, Object data) {
        ExchangeProtocol.Request.Head head = ExchangeProtocol.Request.Head.newBuilder()
                .setCmd(protocol)
                .setVersion(100101)
                .build();
        ExchangeProtocol.Request.Builder request = ExchangeProtocol.Request.newBuilder();
        request.setHead(head);
        if (Objects.nonNull(data)) {
            request.setBody(ByteString.copyFrom(ProtoBufUtils.serialize(data)));
        }
        return request.build();
    }
}
