package com.lzh.game.socket.core.protocol.codec;

import com.lzh.game.socket.core.message.MessageManager;
import com.lzh.game.socket.core.protocol.MessageSerialize;
import com.lzh.game.socket.core.protocol.MessageSerializeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class MessageDecoder extends RemoteMessageDecoder {

    private MessageManager manager;

    @Override
    public Object decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, int msgId, int dataLen) throws Exception {
        if (!manager.hasMessage(msgId)) {
            log.warn("Not defined msgId [{}]", msgId);
            return null;
        }
        int serializeType = manager.getSerializeType(msgId);
        MessageSerialize handler = MessageSerializeManager.getInstance()
                .getProtocolMessage(serializeType);
        if (Objects.isNull(handler)) {
            log.warn("Not defined msg serialize type [{}-{}]", msgId, serializeType);
            return null;
        }
        return handler.decode(msgId, in);
    }
}
