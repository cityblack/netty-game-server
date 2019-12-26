package com.lzh.game.socket.dispatcher;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.lzh.game.common.serialization.SerializationUtil;
import com.lzh.game.socket.exchange.coder.ExchangeProtocol;

import java.util.Objects;

public class ProtoUtil {

    public static MessageOrBuilder toProBufResponse(int cmd, Object data) {
        byte[] bytes = Objects.isNull(data) ? new byte[]{} : SerializationUtil.serialize(data);

        ExchangeProtocol.Response response = ExchangeProtocol
                .Response
                .newBuilder()
                .setHead(ExchangeProtocol.Response.Head.newBuilder()
                        .setCmd(cmd)
                        .setLen(bytes.length)
                        .build())
                .setData(ByteString.copyFrom(bytes))
                .build();
        return response;
    }

    public static <T>T deSerialize(byte[] data, Class<T> target) {
        if (target.isInterface()) {
            throw new IllegalArgumentException("ProtoBuff convert class is interface.");
        }
        return SerializationUtil.deSerialize(data, target);
    }
}
